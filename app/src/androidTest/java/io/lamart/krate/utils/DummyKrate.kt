package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


open class DummyKrate : Krate {
    override fun <T> get(key: String): Maybe<T> = Maybe.empty()
    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> = Flowable.empty()
    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> = Flowable.empty()
    override fun remove(key: String): Completable = Completable.complete()
    override fun <T> put(key: String, value: T): Completable = Completable.complete()
}