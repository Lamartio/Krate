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