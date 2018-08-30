package io.lamart.krate

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SerializationTests {

    @Test
    fun addition_isCorrect() {
        val result = serialize(
                { writeObject(123) },
                { readObject() }
        )

        assertEquals(123, result)
    }

    private fun <T> serialize(
            write: ObjectOutputStream.() -> Unit,
            read: ObjectInputStream.() -> T
    ): T = ByteArrayOutputStream()
            .also { ObjectOutputStream(it).apply(write) }
            .toByteArray()
            .also(::println)
            .let(::ByteArrayInputStream)
            .use(::ObjectInputStream)
            .run(read)
}
