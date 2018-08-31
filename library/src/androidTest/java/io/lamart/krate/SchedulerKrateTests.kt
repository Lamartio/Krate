package io.lamart.krate

import android.support.test.InstrumentationRegistry
import io.lamart.krate.directory.DirectoryKrate
import io.lamart.krate.helpers.KrateTestsSource
import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File


class SchedulerKrateTests : KrateTestsSource {

    private val ioScheduler = TestScheduler()
    private val networkScheduler = TestScheduler()
    private val resultScheduler = TestScheduler()

    private lateinit var directory: File
    private lateinit var krate: Krate

    @Before
    fun setup() {
        directory = InstrumentationRegistry
                .getContext()
                .cacheDir
                .apply { mkdirs() }
        krate = DirectoryKrate(directory).let {
            SchedulerKrate(
                    it,
                    ioScheduler,
                    networkScheduler,
                    resultScheduler
            )
        }
    }

    @After
    fun teardown() {
        directory.deleteRecursively()
    }

    @Test
    override fun put() {
        krate.put(KEY, VALUE).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun get() {
        put()
        krate.get<Any>(KEY).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertValue(VALUE)
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun remove() {
        put()
        krate.remove(KEY).test().apply {
            assertNotComplete()
            ioScheduler.triggerActions()
            resultScheduler.triggerActions()
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    override fun getAndFetch_fetch_only() {
        krate.getAndFetch(KEY, { Single.just(VALUE) }).test().apply {
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
        put()
        krate.getAndFetch(KEY, { Single.just(VALUE) }).test().apply {
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
        krate.getOrFetch(KEY, { Maybe.just(VALUE) }).test().apply {
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
        put()
        krate.getOrFetch(KEY, { Maybe.empty<Any>() }).test().apply {
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
        put()
        krate.getOrFetch(KEY, { Maybe.just(VALUE) }).test().apply {
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
        krate.getOrFetch(KEY, { Maybe.empty<Any>() }).test().apply {
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