package io.lamart.krate.utils

import java.io.OutputStream

/**
 * Flushes and closes the OutputStream
 */

fun <R> OutputStream.use(block: (OutputStream) -> R): R {
    try {
        return block(this)
    } finally {
        flush()
        close()
    }
}