package io.lamart.krate.example

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.facebook.soloader.SoLoader
import io.lamart.krate.Krate
import io.lamart.krate.SchedulerKrate
import io.lamart.krate.database.DatabaseKrate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ExampleApplication : Application() {

    val krate: Krate by lazy {
        "example_database"
                .let(::getDatabasePath)
                .apply { createNewFile() }
                .let { SQLiteDatabase.openOrCreateDatabase(it, null) }
                .let { DatabaseKrate(database = it, interceptor = ConcealInterceptor(this)) }
                .createTableIfNotExists()
                .let { SchedulerKrate(krate = it, ioScheduler = Schedulers.io(), resultScheduler = AndroidSchedulers.mainThread()) }
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false);
    }

}

