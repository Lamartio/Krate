package io.lamart.krate.database

import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import io.lamart.krate.database.Constants.Companion.KEY
import io.lamart.krate.database.Constants.Companion.MODIFIED
import io.lamart.krate.database.Constants.Companion.VALUE

internal class KrateCursor(cursor: Cursor) : Cursor by cursor, Constants, Iterable<KrateCursor> {

    val key: String get() = getColumnIndex(KEY).let(::getString)
    val value: ByteArray get() = getColumnIndex(VALUE).let(::getBlob)
    val modified: Long get() = getColumnIndex(MODIFIED).let(::getLong)

    override fun iterator(): Iterator<KrateCursor> = object : Iterator<KrateCursor> {

        override fun hasNext(): Boolean = moveToNext() && !isAfterLast

        override fun next(): KrateCursor = this@KrateCursor

    }

}

internal fun SQLiteDatabase.query(
        table: String,
        columns: Array<String>? = null,
        distinct: Boolean = false,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        groupBy: String? = null,
        having: String? = null,
        orderBy: String? = null,
        limit: String? = null
): KrateCursor =
        queryWithFactory(
                { _, driver, editTable, query -> SQLiteCursor(driver, editTable, query).let(::KrateCursor) },
                distinct,
                table,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit
        ) as KrateCursor