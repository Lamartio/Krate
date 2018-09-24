/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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