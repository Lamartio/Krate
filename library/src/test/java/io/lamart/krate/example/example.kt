/*
 * Copyright (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.example

import android.content.Context
import io.lamart.krate.Interceptor
import io.lamart.krate.Krate
import io.lamart.krate.Serializer
import io.lamart.krate.database.DatabaseKrate
import io.lamart.krate.directory.DirectoryKrate
import io.reactivex.Flowable
import io.reactivex.Single


class User(val name: String = "Danny", val age: Int = 27)

const val key = "userId"

fun crud(krate: Krate) {
    krate.get<User>(key).subscribe { user -> /* ... */ }
    krate.put<User>(key, User()).subscribe { /* ... */ }
    krate.remove(key).subscribe { /* ... */ }
}


// first gets it from persistence
// next it does the network call and persist the result
// emits 0, 1 or 2 results.

fun network(krate: Krate, getUserFromApi: () -> Single<User>): Flowable<User> =
        krate.getAndFetch("k", getUserFromApi)

fun krates(context: Context, picture: ByteArray) {
    val dirKrate = DirectoryKrate(context.cacheDir)
    val sqlKrate = DatabaseKrate(context.openOrCreateDatabase("test", Context.MODE_PRIVATE, null))

    dirKrate.put("giantPicture", picture).subscribe()
    sqlKrate.put("smallObject", Any())
}


class CustomSerializer : Serializer by Serializer.Default()
class CustomInterceptor : Interceptor by Interceptor.Default

fun customKrate(context: Context) {
    DirectoryKrate(
            directory = context.cacheDir,
            serializer = CustomSerializer(), // swap the default Java serialization
            interceptor = CustomInterceptor() // manipulate the bytes
    )
}

//fun schedulerKrate(krate: Krate): Unit =
//        SchedulerKrate(
//                krate,
//                Schedulers.io(), // io and network operations
//                AndroidSchedulers.mainThread() // result is dispatched to the UI thread
//        )