package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

class FetcherKeyKrate<T>(
        private val krate: Krate,
        private val fetcher: Fetcher,
        private val key: String
) {

    fun get(): Maybe<T> = krate.get(key)

    fun getAndFetch(): Flowable<T> = krate.getAndFetch(key) { fetcher.fetch<T>(key) }

    fun getOrFetch(): Flowable<T> = krate.getOrFetch(key) { fetcher.fetch<T>(key, it) }

    fun remove(): Completable = krate.remove(key)

    fun put(value: T): Completable = krate.put(key, value)

}