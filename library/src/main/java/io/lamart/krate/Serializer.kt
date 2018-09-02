package io.lamart.krate

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream


interface Serializer {

    fun <T> write(output: OutputStream, value: T)

    fun <T> read(input: InputStream): T

    open class Default : Serializer {

        override fun <T> write(output: OutputStream, value: T) =
                ObjectOutputStream(output).writeObject(value)

        @Suppress("UNCHECKED_CAST")
        override fun <T> read(input: InputStream): T =
                ObjectInputStream(input).readObject() as T

    }

}