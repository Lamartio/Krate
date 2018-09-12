package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * An utility Krate that has a predefined key and networking operations.
 */

class KeyFetcherKrate<T>(val krate: Krate, val fetcher: Fetcher, val key: String) {

    fun getModified() =
            krate.getModifieds().flatMapMaybe { modifieds ->
                Maybe.fromCallable<Long> {
                    modifieds[key]
                }
            }

    fun observe(): Flowable<String> = krate.observe().filter { it == key }

    fun get(): Maybe<T> = krate.get(key)

    fun getAndFetch(): Flowable<T> = krate.getAndFetch(key) { fetcher.fetch<T>(key) }

    fun getOrFetch(): Flowable<T> = krate.getOrFetch(key) { fetcher.fetch<T>(key, it) }

    fun remove(): Completable = krate.remove(key)

    fun put(value: T): Completable = krate.put(key, value)

}