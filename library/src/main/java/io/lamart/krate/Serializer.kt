package io.lamart.krate

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream


interface Serializer {

    fun <T> OutputStream.write(value: T)

    fun <T> InputStream.read(key: String): T

    open class Default : Serializer {

        override fun <T> OutputStream.write(value: T) =
                ObjectOutputStream(this).writeObject(value)

        @Suppress("UNCHECKED_CAST")
        override fun <T> InputStream.read(key: String): T =
                ObjectInputStream(this).readObject() as T

    }

}