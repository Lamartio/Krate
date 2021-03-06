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

import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber


interface KrateTestsSource {

    fun observe()

    fun getKeys()

    fun getModifieds()

    fun getModified()

    fun put()

    fun get()

    fun remove()

    fun getAndFetch_both()

    fun getAndFetch_fetch_only()

    fun getOrFetch_both()

    fun getOrFetch_none()

    fun getOrFetch_fetch_only()

    fun getOrFetch_get_only()

    fun fetch()

    fun <T> TestSubscriber<T>.assertNothing() = apply {
        assertNoValues()
        assertNoErrors()
        assertNotComplete()
    }

    fun <T> TestSubscriber<T>.assertEnd(vararg values: T) = apply {
        assertValues(*values)
        assertNoErrors()
        assertComplete()
    }

    fun <T> TestObserver<T>.assertNothing() = apply {
        assertNoValues()
        assertNoErrors()
        assertNotComplete()
    }

    fun <T> TestObserver<T>.assertEnd(vararg values: T) = apply {
        assertValues(*values)
        assertNoErrors()
        assertComplete()
    }

}