package io.lamart.krate

import java.io.InputStream
import java.io.OutputStream

/**
 * The Interceptor gives the developer the change to manipulate the bytes that goes in -and out of persistence.
 *
 * This can be a oppurtunity to encrypt -or decrypte the serialized values.
 */

interface Interceptor {

    /**
     * Grants the option to manipulate the bytes that will be written to persistence.
     *
     * @return either the output or a new wrapper OutputStream
     */

    fun write(key: String, output: OutputStream): OutputStream

    /**
     * Grants the option to manipulate the bytes that will be read from persistence.
     *
     * @return either the input or a new wrapper InputStrea
     */

    fun read(key: String, input: InputStream): InputStream


    /**
     * This implementation of the Interceptor does nothing with both streams.
     */

    object Default : Interceptor {

        override fun write(key: String, output: OutputStream): OutputStream = output

        override fun read(key: String, input: InputStream): InputStream = input

    }

}