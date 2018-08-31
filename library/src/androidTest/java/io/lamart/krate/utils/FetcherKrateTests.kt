package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.lamart.krate.helpers.*
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
    private lateinit var keyKrate: FetcherKrate

    @Before
    fun before() {
        krate = spy(DummyKrate())
        fetcher = spy(DummyFetcher())
        keyKrate = krate.with(fetcher)
    }

    @Test
    fun put() {
        keyKrate.put(KEY, VALUE).test().assertNoErrors().assertComplete()
        verify(krate).put(KEY, VALUE)
    }

    @Test
    fun get() {
        keyKrate.get<Any>(KEY).test().assertNoErrors().assertComplete()
        verify(krate).get<Any>(KEY)
    }

    @Test
    fun remove() {
        keyKrate.remove(KEY).test().assertNoErrors().assertComplete()
        verify(krate).remove(KEY)
    }

    @Test
    fun getAndFetch() {
        keyKrate.getAndFetch<Any>(KEY).test().assertNoErrors().assertComplete()
        verify(fetcher).fetch<Any>(KEY)
    }

    @Test
    fun getOrFetch() {
        keyKrate.getOrFetch<Any>(KEY).test().assertNoErrors().assertComplete()
        verify(fetcher).fetch<Any>(KEY, Long.MIN_VALUE)
    }


    @Test
    fun with() {
        assertThat(
                keyKrate.with<Any>(KEY),
                IsInstanceOf(KeyFetcherKrate::class.java)
        )
    }

}