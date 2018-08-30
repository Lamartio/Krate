package io.lamart.krate

import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream


interface Serializer {

    fun <T> getFlags(value: T): Int

    fun <T> OutputStream.write(value: T, flags: Int)

    fun <T> InputStream.read(key: String, flags: Int): T

    open class Default : Serializer {

        override fun <T> getFlags(value: T): Int =
                (value as? Boolean)?.let { BOOLEAN }
                        ?: (value as? Byte)?.let { BYTE }
                        ?: (value as? Char)?.let { CHAR }
                        ?: (value as? Short)?.let { SHORT }
                        ?: (value as? Int)?.let { INTEGER }
                        ?: (value as? Long)?.let { LONG }
                        ?: (value as? Float)?.let { FLOAT }
                        ?: (value as? Double)?.let { DOUBLE }
                        ?: (value as? ByteArray)?.let { BYTE_ARRAY }
                        ?: (value as? String)?.let { STRING }
                        ?: OBJECT

        override fun <T> OutputStream.write(value: T, flags: Int) =
                ObjectOutputStream(this).run {
                    when (flags) {
                        BOOLEAN -> writeBoolean(value as Boolean)
                        BYTE -> (value as Byte).toInt().let(::writeByte)
                        CHAR -> (value as Char).toInt().let(::writeChar)
                        SHORT -> (value as Short).toInt().let(::writeShort)
                        INTEGER -> writeInt(value as Int)
                        LONG -> writeLong(value as Long)
                        FLOAT -> writeFloat(value as Float)
                        DOUBLE -> writeDouble(value as Double)
                        BYTE_ARRAY -> write(value as ByteArray)
                        STRING -> writeUTF(value as String)
                        else -> writeObject(value)
                    }
                }

        @Suppress("UNCHECKED_CAST")
        override fun <T> InputStream.read(key: String, flags: Int): T =
                ObjectInputStream(this).run {
                    when (flags) {
                        BOOLEAN -> readBoolean() as T
                        BYTE -> readByte() as T
                        CHAR -> readChar() as T
                        SHORT -> readShort() as T
                        INTEGER -> readInt() as T
                        LONG -> readLong() as T
                        FLOAT -> readFloat() as T
                        DOUBLE -> readDouble() as T
                        BYTE_ARRAY -> readBytes(2048) as T
                        STRING -> readUTF() as T
                        else -> readObject() as T
                    }
                }

        companion object {

            const val BOOLEAN = 1
            const val BYTE = 2
            const val CHAR = 4
            const val SHORT = 8
            const val INTEGER = 16
            const val LONG = 32
            const val FLOAT = 64
            const val DOUBLE = 128
            const val BYTE_ARRAY = 256
            const val STRING = 512
            const val OBJECT = 1024

        }

    }

}