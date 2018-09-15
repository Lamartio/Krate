# Krate
[ ![Download](https://api.bintray.com/packages/lamartio/maven/krate/images/download.svg) ](https://bintray.com/lamartio/maven/krate/_latestVersion) 
[ ![Coverage](https://img.shields.io/badge/Coverage-94%25-brightgreen.svg) ](https://bintray.com/lamartio/maven/krate/_latestVersion) 

Simple storage and network layer for Android. It is a key value store that accept any object as its value. Additionally it provides utility for encryption and custom serializers.

A Krate is an interface that has implementations for SQLiteDatabase, Files and in-memory. It supplies its operations in a reactive fashion:

``` kotlin
fun crud(krate: Krate) {
    val getter: Maybe<User> = krate.get("k")

    getter.subscribe { user -> }

    krate.put("k", User()) // lets store a new user
            .andThen { krate.remove("k") } // and then remove it 
            .subscribe()
}
```

Apps often rely on a webservice to provide objects. Krate is great for this data centric approach, since it persists the result of a network call before passing it through.

```kotlin
// first gets it from persistence
// next it does the network call and persist the result
// emits 0, 1 or 2 results.

fun network(krate: Krate, getUserFromApi: () -> Single<User>): Flowable<User> = 
        krate.getAndFetch("k", getUserFromApi)
```

Based on your needs you can decide you persistence method. Image are often not welcome to a database, so Krate can manage a directory for you:

```kotlin
fun krates(context: Context, picture: ByteArray) {
    val dirKrate = DirectoryKrate(context.cacheDir)
    val sqlKrate = DatabaseKrate(context.openOrCreateDatabase("test", Context.MODE_PRIVATE, null))
    
    dirKrate.put("giantPicture", picture).subscribe()
    sqlKrate.put("smallObject", Any())
}
```

You may have the need of encryption or you may want use an alternative serializer. Krate's implementations provide easy access for this use cases:
```kotlin
fun customKrate(context: Context) {
    DirectoryKrate(
            directory = context.cacheDir,
            serializer = CustomSerializer(), // swap the default Java serialization 
            interceptor = CustomInterceptor() // manipulate the bytes
    )
}
```

Copyright
