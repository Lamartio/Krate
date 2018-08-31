package io.lamart.krate.helpers

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

class FetcherKrate(private val krate: Krate, private val fetcher: Fetcher) {

    fun <T> get(key: String): Maybe<T> = krate.get(key)

    fun <T> getAndFetch(key: String): Flowable<T> = krate.getAndFetch(key) { fetcher.fetch<T>(key) }

    fun <T> getOrFetch(key: String): Flowable<T> = krate.getOrFetch(key) { fetcher.fetch<T>(key, it) }

    fun remove(key: String): Completable = krate.remove(key)

    fun <T> put(key: String, value: T): Completable = krate.put(key, value)

    fun <T> with(key: String): KeyFetcherKrate<T> = KeyFetcherKrate(krate, fetcher, key)

}