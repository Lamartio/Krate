package io.lamart.krate


interface KrateTestsSource {

    fun observe()

    fun getKeys()

    fun getModifieds()

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