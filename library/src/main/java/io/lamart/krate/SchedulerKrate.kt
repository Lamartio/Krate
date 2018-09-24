/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
 * @param resultScheduler Is used for presenting the result of any operation.
 *
 */


@Suppress("MoveLambdaOutsideParentheses")
class SchedulerKrate(
        private val krate: Krate,
        private val ioScheduler: Scheduler = Schedulers.trampoline(),
        private val resultScheduler: Scheduler = Schedulers.trampoline()
) : Krate {

    override fun getKeys(): Single<Collection<String>> =
            krate.getKeys().schedule()

    override fun getModifieds(): Single<Map<String, Long>> =
            krate.getModifieds().schedule()

    override fun observe(): Flowable<String> =
            krate.observe().schedule()

    override fun <T> get(key: String): Maybe<T> =
            krate.get<T>(key).schedule()

    override fun getModified(key: String): Maybe<Long> = krate.getModified(key).schedule()

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            krate.getAndFetch(key, fetch).schedule()

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            krate.getOrFetch(key, fetch).schedule()

    override fun <T> fetch(key: String, fetch: () -> Single<T>): Single<T> =
            krate.fetch(key, fetch).schedule()

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