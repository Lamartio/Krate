/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.helpers

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


open class DummyKrate(private val pair: Pair<String, Any?>? = null) : Krate {

    override fun getKeys(): Single<Collection<String>> =
            Single.fromCallable<Collection<String>> {
                pair?.first?.let { listOf(it) } ?: emptyList()
            }

    override fun getModifieds(): Single<Map<String, Long>> =
            Single.fromCallable<Map<String, Long>> {
                pair?.first?.let { mapOf(it to Long.MIN_VALUE) } ?: emptyMap()
            }

    override fun getModified(key: String): Maybe<Long> =
            Maybe.fromCallable {
                when {
                    pair != null -> Long.MIN_VALUE
                    else -> null
                }
            }

    override fun observe(): Flowable<String> =
            pair?.first?.let { Flowable.just(it) } ?: Flowable.empty()

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): Maybe<T> = Maybe.fromCallable { pair?.second as? T }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch().toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch(Long.MIN_VALUE)
            )

    override fun <T> fetch(key: String, fetch: () -> Single<T>): Single<T> =
            fetch()

    override fun remove(key: String): Completable =
            Completable.complete()

    override fun <T> put(key: String, value: T): Completable =
            Completable.complete()

}