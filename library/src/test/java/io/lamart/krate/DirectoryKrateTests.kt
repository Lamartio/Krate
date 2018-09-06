package io.lamart.krate

import android.os.Build
import io.lamart.krate.directory.DirectoryKrate
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@Config(sdk = [Build.VERSION_CODES.JELLY_BEAN_MR2])
@RunWith(RobolectricTestRunner::class)
class DirectoryKrateTests() : KrateTests(), KrateTestsSource {

    override lateinit var krate: Krate

    private lateinit var directory: File

    @Before
    fun setup() {
        directory = RuntimeEnvironment
                .application
                .cacheDir
                .apply { mkdirs() }
        krate = DirectoryKrate(directory)
    }

    @After
    fun teardown() {
        directory.deleteRecursively()
    }

}