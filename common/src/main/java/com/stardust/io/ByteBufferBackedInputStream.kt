package com.stardust.io

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

class ByteBufferBackedInputStream(private var buf: ByteBuffer) : InputStream() {

    @Throws(IOException::class)
    override fun read(): Int {
        return if (!buf.hasRemaining()) {
            -1
        } else buf.get().toInt() and 0xFF
    }

    @Throws(IOException::class)
    override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        if (!buf.hasRemaining()) {
            return -1
        }
        val read = Math.min(len, available())
        buf.get(bytes, off, read)
        buf.position(buf.position() - read)
        return read
    }

    override fun available(): Int {
        return buf.position()
    }
}