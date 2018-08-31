package io.lamart.krate.helpers

import io.lamart.krate.Krate
import io.lamart.krate.helpers.Objects.VALUE
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


open class DummyKrate : Krate {

    override fun <T> get(key: String): Maybe<T> = Maybe.empty()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Single.concat(Single.just(VALUE as T), fetch())

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(Maybe.just(VALUE as T), fetch(Long.MIN_VALUE))

    override fun remove(key: String): Completable = Completable.complete()

    override fun <T> put(key: String, value: T): Completable = Completable.complete()

}