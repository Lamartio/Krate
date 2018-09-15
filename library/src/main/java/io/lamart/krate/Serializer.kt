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
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream

/**
 * The Serializer contains the methods for writing and reading an object to and from bytes respectively.
 */

interface Serializer {

    /**
     * When invoked, the given value should be written to the OutputStream
     *
     * @param key: The key where the value will be associated with
     * @param value: The value that needs to be written to bytes.
     * @param output: The stream where the value needs to be written to.
     */

    fun <T> write(key: String, value: T, output: OutputStream)

    /**
     * When invoked, the given value should be read to the InputStream
     *
     * @param key: The key where the value is associated with
     * @param input: The stream where the value needs to be read from.
     */

    fun <T> read(key: String, input: InputStream): T


    /**
     * This default implementation uses an ObjectOutputStream to convert an object to bytes and an  ObjectInputStream to read an object from bytes.
     */

    open class Default : Serializer {

        override fun <T> write(key: String, value: T, output: OutputStream) =
                ObjectOutputStream(output).writeObject(value)

        @Suppress("UNCHECKED_CAST")
        override fun <T> read(key: String, input: InputStream): T =
                ObjectInputStream(input).readObject() as T

    }

}