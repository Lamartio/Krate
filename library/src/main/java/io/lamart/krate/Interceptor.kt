package io.lamart.krate

import java.io.InputStream
import java.io.OutputStream

interface Interceptor {

    fun write(output: OutputStream): OutputStream

    fun read(input: InputStream): InputStream

    object Default : Interceptor {

        override fun write(output: OutputStream): OutputStream = output

        override fun read(input: InputStream): InputStream = input

    }

}