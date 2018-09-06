package io.lamart.krate.helpers

import io.lamart.krate.Krate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


open class DummyKrate(private val value: Any? = null) : Krate {

    override fun <T> get(key: String): Maybe<T> = Maybe.fromCallable { value as? T }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch().toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch(Long.MIN_VALUE)
            )

    override fun remove(key: String): Completable =
            Completable.complete()

    override fun <T> put(key: String, value: T): Completable =
            Completable.complete()

}