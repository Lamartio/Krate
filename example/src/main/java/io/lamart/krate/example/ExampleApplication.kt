/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.example

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.facebook.soloader.SoLoader
import io.lamart.krate.Krate
import io.lamart.krate.SchedulerKrate
import io.lamart.krate.database.DatabaseKrate
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.lang.Thread.*


class ExampleApplication : Application() {

    @Suppress("ReplaceSingleLineLet")
    val krate: Krate by lazy {
        openOrCreateDatabase("example_database", Context.MODE_PRIVATE, null)
                .let { DatabaseKrate(database = it, interceptor = ConcealInterceptor(this)) }
                .let { SchedulerKrate(krate = it, ioScheduler = Schedulers.io(), resultScheduler = AndroidSchedulers.mainThread()) }
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
    }

}

