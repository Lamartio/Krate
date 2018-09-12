package io.lamart.krate.utils

import io.lamart.DummyFetcher
import io.lamart.krate.Krate
import io.lamart.krate.helpers.DummyKrate
import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify


class FetcherKrateTests {

    private lateinit var krate: Krate
    private lateinit var fetcher: Fetcher
    private lateinit var fetcherKrate: FetcherKrate

    @Before
    fun before() {
        krate = spy(DummyKrate())
        fetcher = spy(DummyFetcher())
        fetcherKrate = FetcherKrate(krate, fetcher)
    }

    @Test
    fun observe() {
        fetcherKrate.observe().test().assertNoErrors().assertComplete()
        verify(krate).observe()
    }

    @Test
    fun getKeys() {
        fetcherKrate.getKeys().test().assertNoErrors().assertComplete()
        verify(krate).getKeys()
    }

    @Test
    fun getModifieds() {
        fetcherKrate.getModifieds().test().assertNoErrors().assertComplete()
        verify(krate).getModifieds()
    }

    @Test
    fun put() {
        fetcherKrate.put(KEY, VALUE).test().assertNoErrors().assertComplete()
        verify(krate).put(KEY, VALUE)
    }

    @Test
    fun get() {
        fetcherKrate.get<Any>(KEY).test().assertNoErrors().assertComplete()
        verify(krate).get<Any>(KEY)
    }

    @Test
    fun remove() {
        fetcherKrate.remove(KEY).test().assertNoErrors().assertComplete()
        verify(krate).remove(KEY)
    }

    @Test
    fun getAndFetch() {
        fetcherKrate.getAndFetch<Any>(KEY).test().assertValueCount(1).assertNoErrors().assertComplete()
        verify(krate).get<Any>(KEY)
        verify(fetcher).fetch<Any>(KEY)
    }

    @Test
    fun getOrFetch() {
        fetcherKrate.getOrFetch<Any>(KEY).test().assertNoErrors().assertComplete()
        verify(krate).get<Any>(KEY)
        verify(fetcher).fetch<Any>(KEY, Long.MIN_VALUE)
    }


    @Test
    fun with() {
        assertThat(
                fetcherKrate.with<Any>(KEY),
                IsInstanceOf(KeyFetcherKrate::class.java)
        )
    }

}