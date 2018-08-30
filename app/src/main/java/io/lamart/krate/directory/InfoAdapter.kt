package io.lamart.krate.directory

interface InfoAdapter {

    fun String.containsKey(key: String): Boolean

    /**
     * The result will function as the file name.
     */

    fun encode(key: String, flags: Int, modified: Long): String

    fun decode(name: String): Info

    open class Default(protected val separator: String = " ") : InfoAdapter {

        override fun String.containsKey(key: String): Boolean = startsWith("$key$separator")

        override fun encode(key: String, flags: Int, modified: Long): String =
                "$key$separator$flags$separator$modified"

        override fun decode(name: String): Info =
                name.split(separator).run {
                    Info(
                            get(0),
                            get(1).toInt(),
                            get(2).toLong()
                    )
                }

    }

}