/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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