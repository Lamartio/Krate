package io.lamart.krate

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.lamart.krate.directory.DirectoryKrate
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class DirectoryKrateTests : KrateTests() {

    override lateinit var krate: Krate

    private lateinit var directory: File

    @Before
    fun setup() {
        directory = InstrumentationRegistry
                .getContext()
                .cacheDir
                .apply { mkdirs() }
        krate = DirectoryKrate(directory)
    }

    @After
    fun teardown() {
        directory.deleteRecursively()
    }

}