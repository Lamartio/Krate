/*
 * Copyright (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.lamart.krate.Interceptor
import io.lamart.krate.Krate
import io.lamart.krate.Serializer
import io.lamart.krate.database.Constants.Companion.KEY
import io.lamart.krate.database.Constants.Companion.MODIFIED
import io.lamart.krate.database.Constants.Companion.VALUE
import io.lamart.krate.use
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.processors.PublishProcessor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * This Krate is associated with a database. It will create one table, if it does not exists, that contains a column for the key, value and modified time.
 *
 * @param tableName The table name that will be used as a name for the single table.
 * @param serializer Serializes and deserializes values to and from bytes respectively.
 * @param interceptor Optionally manipulates the bytes.
 */

class DatabaseKrate(
        private val database: SQLiteDatabase,
        private val tableName: String = "krate_data",
        private val serializer: Serializer = Serializer.Default(),
        private val interceptor: Interceptor = Interceptor.Default
) : Krate, Constants {

    private val processor = PublishProcessor.create<String>()

    init {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS $tableName (" +
                        "$KEY TEXT UNIQUE PRIMARY KEY NOT NULL," +
                        "$MODIFIED LONG NOT NULL," +
                        "$VALUE BLOB NOT NULL" +
                        ");" +
                        "CREATE UNIQUE INDEX IF NOT EXISTS ${tableName}_${KEY}_index ON $tableName ($KEY);"
        )
    }

    override fun getKeys(): Single<Collection<String>> =
            Single.fromCallable {
                queryAll(arrayOf(KEY)).use { cursor ->
                    cursor.map { it.key }
                }
            }

    override fun getModifieds(): Single<Map<String, Long>> =
            Single.fromCallable {
                queryAll(arrayOf(KEY, MODIFIED)).use { cursor ->
                    cursor.map { it.key to it.modified }.toMap()
                }
            }

    override fun observe(): Flowable<String> = processor

    override fun <T> get(key: String): Maybe<T> =
            Maybe.fromCallable<T> {
                queryKey(key).takeIf { it.moveToFirst() }
                        ?.value
                        ?.let(::ByteArrayInputStream)
                        ?.let { interceptor.read(key, it) }
                        ?.use { serializer.read(key, it) }
            }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch(key, fetch).toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    queryModified(key)
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

    override fun <T> fetch(key: String, fetch: () -> Single<T>): Single<T> =
            fetch().flatMap { put(key, it).toSingleDefault(it) }

    override fun remove(key: String): Completable =
            Completable.fromAction {
                database.delete(tableName, "$KEY == ?", arrayOf(key))
            }

    override fun <T> put(key: String, value: T): Completable =
            Completable
                    .fromAction {
                        database.insertWithOnConflict(
                                tableName,
                                KEY,
                                getValues(key, value),
                                SQLiteDatabase.CONFLICT_REPLACE
                        )
                    }
                    .doOnComplete { processor.onNext(key) }

    private fun <T> getValues(key: String, value: T): ContentValues =
            ContentValues(3).apply {
                put(KEY, key)
                put(MODIFIED, System.currentTimeMillis())
                put(VALUE, serialize(key, value))
            }

    private fun <T> serialize(key: String, value: T): ByteArray =
            ByteArrayOutputStream()
                    .also { output ->
                        interceptor
                                .write(key, output)
                                .use { serializer.write(key, value, it) }
                    }
                    .toByteArray()

    private fun queryKey(key: String, columns: Array<String>? = null): KrateCursor =
            database.query(
                    table = tableName,
                    columns = columns,
                    selection = "$KEY == ?",
                    selectionArgs = arrayOf(key)
            )

    private fun queryAll(columns: Array<String>? = null): KrateCursor =
            database.query(tableName, columns = columns)

    override fun getModified(key: String): Maybe<Long> =
            Maybe.fromCallable {
                queryKey(key, arrayOf(MODIFIED)).use {
                    when {
                        it.moveToFirst() -> it.modified
                        else -> null
                    }
                }
            }

    private fun queryModified(key: String): Long =
            queryKey(key, arrayOf(MODIFIED)).use {
                when {
                    it.moveToFirst() -> it.modified
                    else -> Long.MIN_VALUE
                }
            }

}