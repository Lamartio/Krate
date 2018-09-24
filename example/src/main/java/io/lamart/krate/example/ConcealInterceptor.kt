/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.example

import android.content.Context
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.CryptoConfig
import com.facebook.crypto.Entity
import io.lamart.krate.Interceptor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class ConcealInterceptor(context: Context) : Interceptor {

    private val keyChain = SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256)
    private val crypto = AndroidConceal.get().createDefaultCrypto(keyChain)

    override fun write(key: String, output: OutputStream): OutputStream =
            object: ByteArrayOutputStream() {

                override fun close() {
                    crypto.encrypt(toByteArray(), Entity.create(key)).let(output::write)
                    super.close()
                }

            }

    override fun read(key: String, input: InputStream): InputStream =
            input.readBytes(2048)
                    .let { crypto.decrypt(it, Entity.create(key)) }
                    .let(::ByteArrayInputStream)

}