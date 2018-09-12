package io.lamart.krate.directory

interface DirectoryKrateAdapter {

    fun String.containsKey(key: String): Boolean

    /**
     * The result will function as the file name.
     */

    fun encode(key: String, modified: Long): String

    fun getKey(name: String): String

    fun getModified(name: String): Long

    open class Default(protected val separator: String = " ") : DirectoryKrateAdapter {

        override fun String.containsKey(key: String): Boolean = startsWith("$key$separator")

        override fun encode(key: String, modified: Long): String =
                "$key$separator$modified"

        override fun getKey(name: String): String =
                name.split(separator)[0]

        override fun getModified(name: String): Long =
                name.split(separator)[1].toLong()

    }

}