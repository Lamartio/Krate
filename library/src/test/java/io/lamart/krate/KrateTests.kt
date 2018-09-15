/*
 * Copyright (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate

import io.lamart.krate.helpers.Objects.KEY
import io.lamart.krate.helpers.Objects.VALUE
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Test


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
abstract class KrateTests : KrateTestsSource {

    abstract var krate: Krate

    @Test
    override fun observe() {
        val observer = krate.observe().test()

        krate.put(KEY, VALUE)
                .test()
                .assertComplete()
                .assertNoErrors()
        observer
                .assertNotComplete()
                .assertNoErrors()
                .assertValueCount(1)
    }

    @Test
    override fun getKeys() {
        krate.put(KEY, VALUE)
                .test()
                .assertComplete()
                .assertNoErrors()
        krate.getKeys()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue { it.first() == KEY }
    }

    @Test
    override fun getModifieds() {
        krate.put(KEY, VALUE)
                .test()
                .assertComplete()
                .assertNoErrors()
        krate.getModifieds()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue { it[KEY] != null }
    }

    override fun getModified() {
        krate.put(KEY, VALUE)
                .test()
                .assertComplete()
                .assertNoErrors()
        krate.getModified(KEY)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(1)
    }

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
    override fun fetch() {
        krate.fetch(KEY, { Single.just(VALUE) })
                .test()
                .assertResult(VALUE)
    }
}