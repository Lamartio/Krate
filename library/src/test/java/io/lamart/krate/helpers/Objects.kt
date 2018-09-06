package io.lamart.krate.helpers

import java.util.*


object Objects {

    const val KEY = "key"

    val VALUE : Any = ArrayList<Any>().apply {
        add("abc")
        add(123)
        add(Date(0))
    }

}