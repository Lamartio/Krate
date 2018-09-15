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
                    fetch().flatMap { put(key, it).toSingleDefault(it) }.toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get(key),
                    ensureModified(key)
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

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
