package io.lamart.krate.utils

import io.lamart.DummyFetcher
import io.lamart.krate.Krate
import io.lamart.krate.helpers.DummyKrate
import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify


class KeyFetcherKrateTests {

    private lateinit var krate: Krate
    private lateinit var fetcher: Fetcher
    private lateinit var keyFetcherKrate: KeyFetcherKrate<Any>

    @Before
    fun before() {
        krate = spy(DummyKrate())
        fetcher = spy(DummyFetcher())
        keyFetcherKrate = krate.with(fetcher, KEY)
    }

    @Test
    fun observe() {
        keyFetcherKrate.observe().test().assertNoErrors().assertComplete()
        verify(krate).observe()
    }

    @Test
    fun getModified() {
        keyFetcherKrate.getModified().test().assertNoErrors().assertComplete()
        verify(krate).getModifieds()
    }

    @Test
    fun put() {
        keyFetcherKrate.put(VALUE).test().assertNoErrors().assertComplete()
        verify(krate).put(KEY, VALUE)
    }

    @Test
    fun get() {
        keyFetcherKrate.get().test().assertNoErrors().assertComplete()
        verify(krate).get<Any>(KEY)
    }

    @Test
    fun remove() {
        keyFetcherKrate.remove().test().assertNoErrors().assertComplete()
        verify(krate).remove(KEY)
    }

    @Test
    fun getAndFetch() {
        keyFetcherKrate.getAndFetch().test().assertNoErrors().assertComplete()
        verify(fetcher).fetch<Any>(KEY)
    }

    @Test
    fun getOrFetch() {
        keyFetcherKrate.getOrFetch().test().assertNoErrors().assertComplete()
        verify(fetcher).fetch<Any>(KEY, Long.MIN_VALUE)
    }

}