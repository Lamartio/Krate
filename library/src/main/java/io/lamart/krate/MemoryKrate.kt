/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.processors.PublishProcessor

/**
 * An in-memory implementation of a Krate. The data is kept in a Map<String, Pair<Long, *>> and is kept synchronized by a private lock.
 */

class MemoryKrate : Krate {

    private val lock = Any()
    private val map = mutableMapOf<String, Pair<Long, *>>()
    private val processor = PublishProcessor.create<String>()

    override fun getKeys(): Single<Collection<String>> =
            Single.fromCallable {
                synchronized(lock) {
                    map.keys.toSet()
                }
            }

    override fun getModifieds(): Single<Map<String, Long>> =
            Single.fromCallable {
                synchronized(lock) {
                    map.mapValues { it.value.first }
                }
            }

    override fun getModified(key: String): Maybe<Long> =
            Maybe.fromCallable {
                synchronized(lock) {
                    map[key]?.first
                }
            }

    override fun observe(): Flowable<String> = processor

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): Maybe<T> =
            Maybe.fromCallable {
                synchronized(lock) {
                    map[key]?.second as T
                }
            }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch(key, fetch).toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    synchronized(lock) { map[key]?.first ?: Long.MIN_VALUE }
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

    override fun <T> fetch(key: String, fetch: () -> Single<T>): Single<T> =
            fetch().flatMap { put(key, it).toSingleDefault(it) }

    override fun remove(key: String): Completable =
            Completable.fromAction {
                synchronized(lock) {
                    map.remove(key)
                }
            }

    override fun <T> put(key: String, value: T): Completable =
            Completable
                    .fromAction {
                        synchronized(lock) {
                            map.put(key, Pair(System.currentTimeMillis(), value as Any))
                        }
                    }
                    .doOnComplete { processor.onNext(key) }

}