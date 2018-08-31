package io.lamart.krate.helpers


interface KrateTestsSource {

    fun put()

    fun get()

    fun remove()

    fun getAndFetch_both()

    fun getAndFetch_fetch_only()

    fun getOrFetch_both()

    fun getOrFetch_none()

    fun getOrFetch_fetch_only()

    fun getOrFetch_get_only()

}