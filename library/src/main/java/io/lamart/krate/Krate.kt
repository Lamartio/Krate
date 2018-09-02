package io.lamart.krate

import io.lamart.krate.utils.Fetcher
import io.lamart.krate.utils.FetcherKrate
import io.lamart.krate.utils.KeyFetcherKrate
import io.lamart.krate.utils.KeyKrate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface Krate {

    fun <T> get(key: String): Maybe<T>

    fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T>

    fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T>

    fun remove(key: String): Completable

    fun <T> put(key: String, value: T): Completable

    fun <T> with(key: String) = KeyKrate<T>(this, key)

    fun with(fetcher: Fetcher) = FetcherKrate(this, fetcher)

    fun <T> with(fetcher: Fetcher, key: String) = KeyFetcherKrate<T>(this, fetcher, key)

}