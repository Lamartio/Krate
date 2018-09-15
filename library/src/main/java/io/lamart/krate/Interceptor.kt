/*
 * Copyright (c) 2018.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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