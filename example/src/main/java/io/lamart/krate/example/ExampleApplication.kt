package io.lamart.krate.example

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import io.lamart.krate.Krate
import io.lamart.krate.database.DatabaseKrate
import java.io.File


class ExampleApplication : Application() {

    val krate: Krate by lazy {
        cacheDir.apply { mkdirs() }
                .let { File(it, "database") }
                .apply { createNewFile() }
                .let { SQLiteDatabase.openOrCreateDatabase(it, null) }
                .let { DatabaseKrate(it) }
                .createTableIfNotExists()
     }

}