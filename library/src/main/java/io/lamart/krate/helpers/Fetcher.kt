package io.lamart.krate.helpers

import io.reactivex.Maybe
import io.reactivex.Single

interface Fetcher {

    fun <T> fetch(key: String): Single<T>

    fun <T> fetch(key: String, modified: Long): Maybe<T>

}