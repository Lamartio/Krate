package io.lamart.krate.utils

import io.lamart.krate.Krate
import io.lamart.krate.helpers.DummyKrate
import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import io.reactivex.Maybe
import io.reactivex.Single
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class KeyKrateTests {

    private lateinit var krate: Krate
    private lateinit var keyKrate: KeyKrate<Any>

    @Before
    fun before() {
        krate = Mockito.spy(DummyKrate())
        keyKrate = krate.with(KEY)
    }

    @Test
    fun observe() {
        keyKrate.observe().test().assertNoErrors().assertComplete()
        verify(krate).observe()
    }

    @Test
    fun getModified() {
        keyKrate.getModified().test().assertNoErrors().assertComplete()
        verify(krate).getModifieds()
    }

    @Test
    fun put() {
        keyKrate.put(VALUE).test().assertNoErrors().assertComplete()
        verify(krate).put(KEY, VALUE)
    }

    @Test
    fun get() {
        keyKrate.get().test().assertNoErrors().assertComplete()
        verify(krate).get<Any>(KEY)
    }

    @Test
    fun remove() {
        keyKrate.remove().test().assertNoErrors().assertComplete()
        verify(krate).remove(KEY)
    }

    @Test
    fun getAndFetch() {
        val fetch: () -> Single<Any> = { Single.just(VALUE) }

        keyKrate.getAndFetch(fetch).test().assertNoErrors().assertComplete()
        verify(krate).getAndFetch(KEY, fetch)
    }

    @Test
    fun getOrFetch() {
        val fetch: (Long) -> Maybe<Any> = { Maybe.empty() }

        keyKrate.getOrFetch(fetch).test().assertNoErrors().assertComplete()
        verify(krate).getOrFetch(KEY, fetch)
    }

    @Test
    fun with() {
        assertThat(
                keyKrate.with(mock(Fetcher::class.java)),
                IsInstanceOf(KeyFetcherKrate::class.java)
        )
    }

}