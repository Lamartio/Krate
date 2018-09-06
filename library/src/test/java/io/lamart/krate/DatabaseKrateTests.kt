package io.lamart.krate

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import io.lamart.krate.database.DatabaseKrate
import io.lamart.krate.helpers.DummySQLiteOpenHelper
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


@Config(sdk = [Build.VERSION_CODES.JELLY_BEAN_MR2])
@RunWith(RobolectricTestRunner::class)
class DatabaseKrateTests : KrateTests(), KrateTestsSource {

    lateinit var database: SQLiteDatabase
    override lateinit var krate: Krate

    @Before
    fun before() {
        database = RuntimeEnvironment.application.let(::DummySQLiteOpenHelper).writableDatabase
        krate = DatabaseKrate(database)
    }

    @After
    fun after() {
        database.close()
    }

}

