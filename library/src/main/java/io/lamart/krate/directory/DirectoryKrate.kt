package io.lamart.krate.directory

import io.lamart.krate.Interceptor
import io.lamart.krate.Krate
import io.lamart.krate.Serializer
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.File

@Suppress("MoveLambdaOutsideParentheses")
class DirectoryKrate(
        private val directory: File,
        private val serializer: Serializer = Serializer.Default(),
        private val interceptor: Interceptor = Interceptor.Default,
        private val adapter: DirectoryKrateAdapter = DirectoryKrateAdapter.Default()
) : Krate {

    override fun <T> get(key: String): Maybe<T> =
            Maybe.fromCallable {
                directory.find(key)?.let { file ->
                    file.inputStream()
                            .let { interceptor.read(key, it) }
                            .use { input -> serializer.read<T>(input) }
                }
            }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch().flatMap { put(key, it).toSingleDefault(it) }.toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get(key),
                    getModified(key)
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

    override fun remove(key: String): Completable =
            Completable.fromAction { directory.find(key)?.delete() }

    override fun <T> put(key: String, value: T): Completable =
            Completable.fromAction {
                adapter.encode(key, System.currentTimeMillis())
                        .let { File(directory, it) }
                        .apply { createNewFile() }
                        .outputStream()
                        .let { interceptor.write(key, it) }
                        .let { output ->
                            try {
                                serializer.write(output, value)
                            } finally {
                                output.flush()
                                output.close()
                            }
                        }
            }

    private fun File.find(key: String): File? =
            listFiles({ _, name -> adapter.run { name.containsKey(key) } }).firstOrNull()

    private fun getModified(key: String) =
            directory
                    .find(key)
                    ?.name
                    ?.let(adapter::getModified)
                    ?: Long.MIN_VALUE

}
