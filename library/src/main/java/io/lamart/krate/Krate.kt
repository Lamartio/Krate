/*
 * Copyright (c) 2018.
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

/**
 * A krate represents a simplified API from reading and writing from a data source. As an addition, a Krate can perform network operations ands write their result to the data source afterwards.
 *
 * Each object that is written to the data source is retrievable be key (Stirng). Thereby it is best to think of this API as a key-value store.
 *
 */

interface Krate {

    /**
     * Queries the underlying datasource for all the keys currently available. Note that the result does not mutate when values are added or removed.
     */

    fun getKeys(): Single<Collection<String>>

    /**
     * Queries the underlying datasource for all the keys with their modified time currently available. Note that the result does not mutate when values are added or removed.
     *
     * The value of this Map is always Unix epoch time.
     */

    fun getModifieds(): Single<Map<String, Long>>

    /**
     * Return the modified date associated with this key.
     */

    fun getModified(key: String) : Maybe<Long>

    /**
     * Returns a stream that emits a key, every time a value gets added, updated or removed.
     */

    fun observe(): Flowable<String>

    /**
     * Returns a Maybe that contains the value associated with this key. It could be that no value is yet associated and thereby this result is empty.
     */

    fun <T> get(key: String): Maybe<T>

    /**
     * Removes the value associated with this key.
     */

    fun remove(key: String): Completable

    /**
     * Adds or updates the value associated with this key.
     */

    fun <T> put(key: String, value: T): Completable

    /**
     * Returns a utility Krate that will fixate the key. When using its results, the key parameter is pre-filled.
     */

    /**
     * This will return 0-2 result dependent on the following steps
     *
     * - If there is a value available for this key, it will be emitted.
     * - If the fetch function succeeds, the value will be associated with the key and it will be emitted.
     *
     * When both of them fail, the result will be empty.
     * When one of them fails, the result will emit one value.
     *
     * There is a slight performance gain in comparison with getOrFetch() since this will not query and validate the modified time.
     *
     */

    fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T>

    /**
     * This will return 0-2 result dependent on the following steps
     *
     * - If there is a value available for this key, it will be emitted.
     * - If the fetch function succeeds, the value will be associated with the key and it will be emitted. The fetch function may return an empty Maybe which will skipp persistence and emittion.
     *
     * When both of them fail, the result will be empty.
     * When one of them fails, the result will emit one value.
     *
     * There is a slight performance overhead in comparison with getAndFetch() since this will query and validate the modified time. Though the performance overhead may be neglegtable dependent on the implementation.
     *
     */

    fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T>

    /**
     * This will call the fetch paramater and persist associate the result with the given key.
     */

    fun <T> fetch(key: String, fetch: () -> Single<T>): Single<T>

}