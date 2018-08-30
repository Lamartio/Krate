package io.lamart.krate.database

import android.provider.BaseColumns

internal interface Constants {

    companion object {

        const val KEY = BaseColumns._ID
        const val MODIFIED = "modified"
        const val VALUE = "value"

    }
}