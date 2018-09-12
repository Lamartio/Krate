package io.lamart.krate.utils

import io.reactivex.Maybe
import io.reactivex.Single

interface Fetcher {

    /**
     * Is called when Krate.getAndFetch is called and after Krate.get() is called. This can be used to fetch the value from network and after that the key will be updated with the retrieved value.
     */

    fun <T> fetch(key: String): Single<T>

    /**
     * Is called when Krate.getOrFetch is called and after Krate.get() is called. This can be used to fetch the value from network after that the key will be updated with the retrieved value.
     *
     * During this operation, you can decide not to return anything and thus return an empty Maybe.
     */

    fun <T> fetch(key: String, modified: Long): Maybe<T>

}