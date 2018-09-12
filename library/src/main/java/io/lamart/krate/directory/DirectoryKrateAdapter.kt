package io.lamart.krate.directory

interface DirectoryKrateAdapter {


    /**
     * Metadata that is associated with the value must be encoded in a single String. This will function as the file's name and must be unique and respect the file name conventions.
     */

    fun encode(key: String, modified: Long): String

    /**
     * This function must retrieve the key associated with the value from the encoded name.
     */

    fun getKey(name: String): String

    /**
     * This function must retrieve the modified date from the encoded name.
     */

    fun getModified(name: String): Long

    /**
     * A default implementation that uses a predefined seperator to seperate the key from the modfied date.
     */

    open class Default(protected val separator: String = " ") : DirectoryKrateAdapter {

        override fun encode(key: String, modified: Long): String =
                "$key$separator$modified"

        override fun getKey(name: String): String =
                name.split(separator)[0]

        override fun getModified(name: String): Long =
                name.split(separator)[1].toLong()

    }

}