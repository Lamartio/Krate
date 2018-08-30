package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

open class KeyKrate<T>(private val krate: Krate, private val key: String) {

    fun get(): Maybe<T> = krate.get(key)

    fun getAndFetch(fetch: () -> Single<T>): Flowable<T> = krate.getAndFetch(key, fetch)

    fun getOrFetch(fetch: (modified: Long) -> Maybe<T>): Flowable<T> = krate.getOrFetch(key, fetch)

    fun remove(): Completable = krate.remove(key)

    fun put(value: T): Completable = krate.put(key, value)

    fun withFetcher(fetcher: Fetcher) = FetcherKeyKrate<T>(krate, fetcher, key)

}