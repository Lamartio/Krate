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
    private val networkScheduler = TestScheduler()
    private val resultScheduler = TestScheduler()

    private lateinit var valueKrate: Krate
    private lateinit var emptyKrate: Krate

    @Before
    fun setup() {
        valueKrate = SchedulerKrate(
                DummyKrate(KEY to VALUE),
                ioScheduler,
                networkScheduler,
                resultScheduler
        )
        emptyKrate = SchedulerKrate(
                DummyKrate(),
                ioScheduler,
                networkScheduler,
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
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoValues()
            assertNotComplete()
            networkScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValue(VALUE)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getAndFetch_both() {
        valueKrate.getAndFetch(KEY, { Single.just(VALUE) }).test().apply {
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueAt(0, VALUE)
            assertNotComplete()
            networkScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueAt(1, VALUE)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getOrFetch_fetch_only() {
        emptyKrate.getOrFetch(KEY, { Maybe.just(VALUE) }).test().apply {
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoValues()
            assertNotComplete()
            networkScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValue(VALUE)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getOrFetch_get_only() {
        valueKrate.getOrFetch(KEY, { Maybe.empty<Any>() }).test().apply {
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValue(VALUE)
            assertNotComplete()
            networkScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValue(VALUE)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getOrFetch_both() {
        valueKrate.getOrFetch(KEY, { Maybe.just(VALUE) }).test().apply {
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueAt(0, VALUE)
            assertNotComplete()
            networkScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValueAt(1, VALUE)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getOrFetch_none() {
        emptyKrate.getOrFetch(KEY, { Maybe.empty<Any>() }).test().apply {
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoValues()
            assertNotComplete()
            networkScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoValues()
            assertNoErrors()
            assertComplete()
        }
    }

}