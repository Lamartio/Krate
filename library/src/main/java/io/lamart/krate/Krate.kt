package io.lamart.krate

import io.lamart.krate.utils.Fetcher
import io.lamart.krate.utils.FetcherKrate
import io.lamart.krate.utils.KeyFetcherKrate
import io.lamart.krate.utils.KeyKrate
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
     * Returns a stream that emits a key, every time a value gets added, updated or removed.
     */

    fun observe(): Flowable<String>

    /**
     * Returns a Maybe that contains the value associated with this key. It could be that no value is yet associated and thereby this result is empty.
     */

    fun <T> get(key: String): Maybe<T>

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

    fun <T> with(key: String) = KeyKrate<T>(this, key)

    /**
     * Returns a utility Krate that will fixate the networking operations by the given parameter.
     *
     * @param fetcher An interface that performs the network operations.
     */

    fun with(fetcher: Fetcher) = FetcherKrate(this, fetcher)

    /**
     * Returns a utility Krate that will both fixate the key and the networking operations. The only variable that is necessary to control this Krate is the value.
     *
     * @param fetcher An interface that performs the network operations.
     */

    fun <T> with(key: String, fetcher: Fetcher) = KeyFetcherKrate<T>(this, fetcher, key)

}