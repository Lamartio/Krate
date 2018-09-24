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

import io.lamart.krate.helpers.DummyKrate
import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class SchedulerKrateTests : KrateTestsSource {

    private val ioScheduler = TestScheduler()
    private val resultScheduler = TestScheduler()

    private lateinit var valueKrate: Krate
    private lateinit var emptyKrate: Krate

    @Before
    fun setup() {
        valueKrate = SchedulerKrate(
                DummyKrate(KEY to VALUE),
                ioScheduler,
                resultScheduler
        )
        emptyKrate = SchedulerKrate(
                DummyKrate(),
                ioScheduler,
                resultScheduler
        )
    }

    @Test
    override fun observe() {
        valueKrate.observe().test().apply {
            assertValueCount(0)
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueCount(1)
            assertNoErrors()
        }
    }

    @Test
    override fun getKeys() {
        valueKrate.getKeys().test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueCount(1)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getModifieds() {
        valueKrate.getModifieds().test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueCount(1)
            assertNoErrors()
            assertComplete()
        }
    }

    override fun getModified() {
        valueKrate.getModified(KEY).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueCount(1)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun put() {
        valueKrate.put(KEY, VALUE).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun get() {
        valueKrate.get<Any>(KEY).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueCount(1)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun remove() {
        valueKrate.remove(KEY).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getAndFetch_fetch_only() {
        emptyKrate.getAndFetch(KEY, { Single.just(VALUE) }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertEnd(VALUE)
        }
    }

    @Test
    override fun getAndFetch_both() {
        valueKrate.getAndFetch(KEY, { Single.just(VALUE) }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertEnd(VALUE, VALUE)
        }
    }

    @Test
    override fun getOrFetch_fetch_only() {
        emptyKrate.getOrFetch(KEY, { Maybe.just(VALUE) }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertEnd(VALUE)
        }
    }

    @Test
    override fun getOrFetch_get_only() {
        valueKrate.getOrFetch(KEY, { Maybe.empty<Any>() }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertEnd(VALUE)
        }
    }

    @Test
    override fun getOrFetch_both() {
        valueKrate.getOrFetch(KEY, { Maybe.just(VALUE) }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertEnd(VALUE, VALUE)
        }
    }

    @Test
    override fun getOrFetch_none() {
        emptyKrate.getOrFetch(KEY, { Maybe.empty<Any>() }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertEnd()
        }
    }

    @Test
    override fun fetch() {
        valueKrate.fetch(KEY, { Single.just(VALUE) }).test().apply {
            assertNothing()
            ioScheduler.triggerActions()
            assertNothing()
            resultScheduler.triggerActions()
            assertResult(VALUE)
        }
    }

}