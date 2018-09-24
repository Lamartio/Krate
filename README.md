# Krate
[ ![Download](https://api.bintray.com/packages/lamartio/maven/krate/images/download.svg) ](https://bintray.com/lamartio/maven/krate/_latestVersion) 
[ ![Coverage](https://img.shields.io/badge/Coverage-90%25-brightgreen.svg) ](https://bintray.com/lamartio/maven/krate/_latestVersion) 

Krate is a persistence layer that offer CRUD operations for a key-value store. Each result is delivered through a RxObservable. Additionally you can sync your offline data with a server through Krate's `fetch` functions.

```kotlin
class User(val name: String = "Danny", val age: Int = 27)

const val key = "userId"

fun crud(krate: Krate) {
    krate.get<User>(key).subscribe { user -> /* ... */ } 
    krate.put<User>(key, User()).subscribe { /* ... */ } 
    krate.remove(key).subscribe { /* ... */ } 
}
```

Apps often rely on a webservice to provide objects. Krate is great for this data centric approach, since it persists the result of a network call before passing it through.

```kotlin
// first gets it from persistence
// next it does the network call and persist the result
// emits 0, 1 or 2 results.

fun network(krate: Krate, getUserFromApi: () -> Single<User>): Flowable<User> = 
        krate.getAndFetch("userId", getUserFromApi)
```

Based on your needs you can decide you persistence method. Image are often not welcome in a database, so Krate can manage a directory for you:

```kotlin
fun krates(context: Context, picture: ByteArray) {
    val dirKrate = DirectoryKrate(context.cacheDir)
    val sqlKrate = DatabaseKrate(context.openOrCreateDatabase("test", Context.MODE_PRIVATE, null))
    
    dirKrate.put("giantPicture", picture).subscribe()
    sqlKrate.put("smallObject", Any())
}
```

You may have the need of encryption for you may want use an alternative serializer. Krate's implementations provide easy access for this use cases:
```kotlin
fun customKrate(context: Context) {
    DirectoryKrate(
            directory = context.cacheDir,
            serializer = CustomSerializer(), // swap the default Java serialization 
            interceptor = CustomInterceptor() // manipulate the bytes
    )
}
```

Krate offers full control over the threads you want to apply your operations on. Just wrap the created Krate in a `SchedulerKrate`.

```kotlin
fun schedulerKrate(krate: Krate): Unit =
        SchedulerKrate(
                krate,
                Schedulers.io(), // io and network operations
                AndroidSchedulers.mainThread() // result is dispatched to the UI thread
        )
```

# License

Copyright 2018 Danny Lamarti.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
