package io.lamart.krate

import java.io.InputStream
import java.io.OutputStream

interface Interceptor {

    fun write(key: String, output: OutputStream): OutputStream

    fun read(key: String, input: InputStream): InputStream

    object Default : Interceptor {

        override fun write(key: String, output: OutputStream): OutputStream = output

        override fun read(key: String, input: InputStream): InputStream = input

    }

}