package io.lamart.krate

import io.lamart.krate.helpers.*
import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import io.reactivex.Maybe
import io.reactivex.Single
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@Ignore
abstract class KrateTests : KrateTestsSource {

    abstract var krate: Krate

    @Test
    override fun put() {
        krate.put(KEY, VALUE).test().assertNoErrors().assertComplete()
    }

    @Test
    override fun get() {
        krate.get<Any>(KEY)
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()
        put()
        krate.get<Any>(KEY)
                .test()
                .assertValue(VALUE)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    override fun remove() {
        krate.apply {
            put()
            remove(KEY).test().assertNoErrors()
            get<Any>(KEY).test()
                    .assertNoValues()
                    .assertNoErrors().assertComplete()
        }
    }

    @Test
    override fun getAndFetch_fetch_only() {
        krate.apply {
            getAndFetch<Any>(KEY, { Single.just(VALUE) })
                    .test()
                    .assertValue(VALUE)
                    .assertNoErrors()
        }
    }

    @Test
    override fun getAndFetch_both() {
        put()
        krate.getAndFetch<Any>(KEY, { Single.just(VALUE) })
                .test()
                .assertValueAt(0, VALUE)
                .assertValueAt(1, VALUE)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    override fun getOrFetch_fetch_only() {
        krate.getOrFetch<Any>(KEY, { Maybe.just(VALUE) })
                .test()
                .assertValue(VALUE)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    override fun getOrFetch_get_only() {
        put()
        krate.getOrFetch<Any>(KEY, { Maybe.empty() })
                .test()
                .assertValue(VALUE)
                .assertNoErrors()
    }

    @Test
    override fun getOrFetch_both() {
        put()
        krate.getOrFetch<Any>(KEY, { Maybe.just(VALUE) })
                .test()
                .assertValueAt(0, VALUE)
                .assertValueAt(1, VALUE)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    override fun getOrFetch_none() {
        krate.getOrFetch<Any>(KEY, { Maybe.empty() })
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun withKey() {
        assertThat(
                krate.with<Any>(KEY),
                IsInstanceOf(KeyKrate::class.java)
        )
    }

    @Test
    fun withFetcher() {
        assertThat(
                krate.with(Mockito.mock(Fetcher::class.java)),
                IsInstanceOf(FetcherKrate::class.java)
        )
    }

    @Test
    fun withKeyFetcher() {
        assertThat(
                krate.with<Any>(Mockito.mock(Fetcher::class.java), KEY),
                IsInstanceOf(KeyFetcherKrate::class.java)
        )
    }

}