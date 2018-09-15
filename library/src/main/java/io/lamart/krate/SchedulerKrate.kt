package io.lamart.krate

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * A wrapper around a Krate that gives control over the thread a certain operation is being executed on.
 *
 * The default value for any scheduler will proceed the operation on the current scheduler.
 *
 * @param ioScheduler Is used for all persistent operations.
 * @param networkScheduler Is used for all fetching operations.
 * @param resultScheduler Is used for presenting the result of any operation.
 *
 */

@Suppress("MoveLambdaOutsideParentheses")
class SchedulerKrate(
        private val krate: Krate,
        private val ioScheduler: Scheduler = Schedulers.trampoline(),
        private val networkScheduler: Scheduler = Schedulers.trampoline(),
        private val resultScheduler: Scheduler = Schedulers.trampoline()
) : Krate {

    override fun getKeys(): Single<Collection<String>> =
            krate.getKeys().schedule()

    override fun getModifieds(): Single<Map<String, Long>> =
            krate.getModifieds().subscribeOn(ioScheduler)

    override fun observe(): Flowable<String> =
            krate.observe().schedule()

    override fun <T> get(key: String): Maybe<T> =
            krate.get<T>(key).schedule()

    override fun getModified(key: String): Maybe<Long> = krate.getModified(key).schedule()

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