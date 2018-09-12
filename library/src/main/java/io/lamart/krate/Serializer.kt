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