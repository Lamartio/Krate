package io.lamart.krate

import org.junit.Before


class MemoryKrateTests : KrateTests(), KrateTestsSource {

    override lateinit var krate: Krate

    @Before
    fun setup() {
        krate = MemoryKrate()
    }

}