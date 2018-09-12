package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * An utility Krate that has a predefined key.
 */

class KeyKrate<T>(val krate: Krate, val key: String) {

    fun getModified() =
            krate.getModifieds().flatMapMaybe { modifieds ->
                Maybe.fromCallable<Long> {
                    modifieds[key]
                }
            }

    fun observe(): Flowable<String> = krate.observe().filter { it == key }

    fun get(): Maybe<T> = krate.get(key)

    fun getAndFetch(fetch: () -> Single<T>): Flowable<T> = krate.getAndFetch(key, fetch)

    fun getOrFetch(fetch: (modified: Long) -> Maybe<T>): Flowable<T> = krate.getOrFetch(key, fetch)

    fun remove(): Completable = krate.remove(key)

    fun put(value: T): Completable = krate.put(key, value)

    fun with(fetcher: Fetcher) = KeyFetcherKrate<T>(krate, fetcher, key)

}