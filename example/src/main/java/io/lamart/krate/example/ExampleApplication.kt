package io.lamart.krate.example

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.facebook.soloader.SoLoader
import io.lamart.krate.Krate
import io.lamart.krate.database.DatabaseKrate
import java.io.File


class ExampleApplication : Application() {

    val krate: Krate by lazy {
        cacheDir.apply { mkdirs() }
                .let { File(it, "database") }
                .apply { createNewFile() }
                .let { SQLiteDatabase.openOrCreateDatabase(it, null) }
                .let { DatabaseKrate(database = it, interceptor = ConcealInterceptor(this)) }
                .createTableIfNotExists()
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false);
    }

}

