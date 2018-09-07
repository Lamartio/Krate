package io.lamart.krate

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


class MemoryKrate : Krate {

    private val lock = Any()
    private val map = mutableMapOf<String, Pair<Long, Any>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): Maybe<T> =
            Maybe.fromCallable {
                synchronized(lock) { map[key]?.second as T }
            }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch().flatMap { put(key, it).toSingleDefault(it) }.toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    synchronized(lock) { map[key]?.first ?: Long.MIN_VALUE }
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

    override fun remove(key: String): Completable =
            Completable.fromAction {
                synchronized(lock) { map.remove(key) }
            }

    override fun <T> put(key: String, value: T): Completable =
            Completable.fromAction {
                synchronized(lock) {
                    map.put(key, Pair(System.currentTimeMillis(), value as Any))
                }
            }

}