package io.lamart

import io.lamart.krate.utils.Fetcher
import io.lamart.krate.utils.Objects
import io.reactivex.Maybe
import io.reactivex.Single

open class DummyFetcher : Fetcher {

    @Suppress("UNCHECKED_CAST")
    override fun <T> fetch(key: String): Single<T> = Single.just(Objects.VALUE as T)

    override fun <T> fetch(key: String, modified: Long): Maybe<T> = Maybe.empty()

}