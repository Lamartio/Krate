package io.lamart.krate

import io.reactivex.*
import io.reactivex.schedulers.Schedulers


@Suppress("MoveLambdaOutsideParentheses")
class SchedulerKrate(
        private val krate: Krate,
        private val ioScheduler: Scheduler = Schedulers.trampoline(),
        private val networkScheduler: Scheduler = Schedulers.trampoline(),
        private val resultScheduler: Scheduler = Schedulers.trampoline()
) : Krate by krate {

    override fun getKeys(): Single<Collection<String>> =
            krate.getKeys().schedule()

    override fun getModifieds(): Single<Map<String, Long>> =
            krate.getModifieds().subscribeOn(ioScheduler)

    override fun observe(): Flowable<String> =
            krate.observe().schedule()

    override fun <T> get(key: String): Maybe<T> =
            krate.get<T>(key).schedule()

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            krate.getAndFetch(key, { fetch().subscribeOn(networkScheduler) }).schedule()

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            krate.getOrFetch(key, { fetch(it).subscribeOn(networkScheduler) }).schedule()

    override fun remove(key: String): Completable = krate.remove(key).schedule()

    override fun <T> put(key: String, value: T): Completable = krate.put(key, value).schedule()

    private fun <T> Flowable<T>.schedule() =
            compose { it.subscribeOn(ioScheduler).observeOn(resultScheduler) }

    private fun Completable.schedule() =
            compose { it.subscribeOn(ioScheduler).observeOn(resultScheduler) }

    private fun <T> Maybe<T>.schedule() =
            compose { it.subscribeOn(ioScheduler).observeOn(resultScheduler) }

    private fun <T> Single<T>.schedule() =
            compose { it.subscribeOn(ioScheduler).observeOn(resultScheduler) }

}