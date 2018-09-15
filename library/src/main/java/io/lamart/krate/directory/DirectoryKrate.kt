/*
 * Copyright (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.directory

import io.lamart.krate.Interceptor
import io.lamart.krate.Krate
import io.lamart.krate.Serializer
import io.lamart.krate.use
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.processors.PublishProcessor
import java.io.File
import java.io.InputStream

/**
 * This Krate assumes that it is given a existing directory in which it can store each value in its own file.
 *
 * NOTE: You are responsible for serving a File that is a created directory.
 *
 * @param directory The directory where all the values will be stored in.
 * @param serializer Serializes and deserializes values to and from bytes respectively.
 * @param interceptor Optionally manipulates the bytes.
 * @param adapter Is responsible for creating proper filenames.
 */

@Suppress("MoveLambdaOutsideParentheses")
class DirectoryKrate(
        private val directory: File,
        private val serializer: Serializer = Serializer.Default(),
        private val interceptor: Interceptor = Interceptor.Default,
        private val adapter: DirectoryKrateAdapter = DirectoryKrateAdapter.Default()
) : Krate {

    private val processor = PublishProcessor.create<String>()

    override fun getKeys(): Single<Collection<String>> =
            Single.fromCallable {
                directory.list().map(adapter::getKey)
            }

    override fun getModifieds(): Single<Map<String, Long>> =
            Single.fromCallable {
                directory
                        .list()
                        .asSequence()
                        .map { adapter.getKey(it) to adapter.getModified(it) }
                        .toMap()
            }

    override fun observe(): Flowable<String> = processor

    override fun <T> get(key: String): Maybe<T> =
            Maybe.fromCallable {
                directory.find(key)
                        ?.inputStream()
                        ?.let { interceptor.read(key, it) }
                        ?.use<InputStream, T> { serializer.read(key, it) }
            }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch(key, fetch).toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get(key),
                    ensureModified(key)
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

    override fun <T> fetch(key: String, fetch: () -> Single<T>): Single<T> =
            fetch().flatMap { put(key, it).toSingleDefault(it) }

    override fun remove(key: String): Completable =
            Completable.fromAction { directory.find(key)?.delete() }

    override fun <T> put(key: String, value: T): Completable =
            Completable
                    .fromAction {
                        File(directory, adapter.encode(key, System.currentTimeMillis()))
                                .apply { createNewFile() }
                                .outputStream()
                                .let { interceptor.write(key, it) }
                                .use { serializer.write(key, value, it) }
                    }
                    .doOnComplete { processor.onNext(key) }

    private fun File.find(key: String): File? =
            listFiles({ _, name -> adapter.getKey(name) == key }).firstOrNull()

    override fun getModified(key: String): Maybe<Long> =
            Maybe.fromCallable {
                directory
                        .find(key)
                        ?.name
                        ?.let(adapter::getModified)
            }


    private fun ensureModified(key: String) =
            directory
                    .find(key)
                    ?.name
                    ?.let(adapter::getModified)
                    ?: Long.MIN_VALUE

}
