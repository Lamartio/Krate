package io.lamart.krate.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DummySQLiteOpenHelper(context: Context) : SQLiteOpenHelper(
       context,
       "path",
       null,
       1
) {

   override fun onCreate(database: SQLiteDatabase) {}

   override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

}