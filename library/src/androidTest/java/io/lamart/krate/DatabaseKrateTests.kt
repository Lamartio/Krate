package io.lamart.krate

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import io.lamart.krate.database.DatabaseKrate
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DatabaseKrateTests : KrateTests() {

    lateinit var database: SQLiteDatabase
    override lateinit var krate: Krate

    @Before
    fun before() {
        database = SQLiteDatabase.OpenParams
                .Builder()
                .build()
                .let(SQLiteDatabase::createInMemory)
        krate = database
                .let { DatabaseKrate(it) }
                .createTableIfNotExists()
    }

    @After
    fun after() {
        database.close()
    }

}