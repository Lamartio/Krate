package io.lamart.krate

import android.database.sqlite.SQLiteDatabase
import android.support.test.runner.AndroidJUnit4
import io.lamart.krate.database.DatabaseKrate
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import java.io.Serializable


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        val database = SQLiteDatabase.OpenParams
                .Builder()
                .build()
                .let(SQLiteDatabase::createInMemory)
        val krate = DatabaseKrate(database)


        krate.put("test", "myPrettyObject").subscribe()
        krate.get<String>("test")
                .test()
                .assertValue { it == "myPrettyObject" }

        krate.put("point", Point(1, 1)).subscribe()
        krate.getAndFetch("point", { Single.just(Point(2, 2)) })
                .test()
                .assertValueAt(0, { it.x == 1 && it.y == 1 })
                .assertValueAt(1, { it.x == 2 && it.y == 2 })
    }
}

data class Point(val x: Int, val y: Int) : Serializable