package io.lamart.krate.database

import android.content.ContentValues
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import io.lamart.krate.Interceptor
import io.lamart.krate.Krate
import io.lamart.krate.Serializer
import io.lamart.krate.database.Constants.Companion.KEY
import io.lamart.krate.database.Constants.Companion.MODIFIED
import io.lamart.krate.database.Constants.Companion.VALUE
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DatabaseKrate(
        private val database: SQLiteDatabase,
        private val tableName: String = "krate_data",
        private val serializer: Serializer = Serializer.Default(),
        private val interceptor: Interceptor = Interceptor.Default
) : Krate, Constants {

    fun createTableIfNotExists(): DatabaseKrate = apply {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS $tableName (" +
                        "$KEY TEXT UNIQUE PRIMARY KEY NOT NULL," +
                        "$MODIFIED LONG NOT NULL," +
                        "$VALUE BLOB NOT NULL" +
                        ");" +
                        "CREATE UNIQUE INDEX IF NOT EXISTS ${tableName}_${KEY}_index ON $tableName ($KEY);"
        )
    }

    override fun <T> get(key: String): Maybe<T> =
            Maybe.fromCallable<T> {
                query(key).takeIf { it.moveToFirst() }?.let { cursor ->
                    cursor.value
                            .let(::ByteArrayInputStream)
                            .let(interceptor::read)
                            .use { input -> serializer.run { input.read<T>(key) } }
                }
            }

    override fun <T> getAndFetch(key: String, fetch: () -> Single<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    fetch().flatMap { put(key, it).toSingleDefault(it) }.toMaybe()
            )

    override fun <T> getOrFetch(key: String, fetch: (modified: Long) -> Maybe<T>): Flowable<T> =
            Maybe.concat(
                    get<T>(key),
                    getModified(key)
                            .let(fetch)
                            .flatMapSingleElement { put(key, it).toSingleDefault(it) }
            )

    override fun remove(key: String): Completable =
            Completable.fromAction {
                database.delete(tableName, "$KEY == ?", arrayOf(key))
            }

    override fun <T> put(key: String, value: T): Completable =
            Completable.fromAction {
                database.insertWithOnConflict(
                        tableName,
                        KEY,
                        getValues(key, value),
                        SQLiteDatabase.CONFLICT_REPLACE
                )
            }

    private fun <T> getValues(key: String, value: T): ContentValues =
            ContentValues(4).apply {
                put(KEY, key)
                put(MODIFIED, System.currentTimeMillis())
                put(VALUE, serialize(value))
            }

    private fun <T> serialize(value: T): ByteArray =
            ByteArrayOutputStream()
                    .also {
                        interceptor.write(it).let { output ->
                            try {
                                serializer.run { output.write(value) }
                            } finally {
                                output.flush()
                                output.close()
                            }
                        }
                    }
                    .toByteArray()

    private fun query(key: String, columns: Array<String>? = null): KrateCursor =
            database.queryWithFactory(
                    { _, driver, editTable, query -> SQLiteCursor(driver, editTable, query).let(::KrateCursor) },
                    false,
                    tableName,
                    columns,
                    "$KEY == ?",
                    arrayOf(key),
                    null,
                    null,
                    null,
                    null
            ) as KrateCursor

    private fun getModified(key: String): Long =
            query(key, arrayOf(MODIFIED)).takeIf { it.moveToFirst() }?.modified ?: Long.MIN_VALUE

}

