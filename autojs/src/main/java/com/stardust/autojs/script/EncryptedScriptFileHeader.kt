package com.stardust.autojs.script

import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

object EncryptedScriptFileHeader {

    const val FLAG_INVALID_FILE: Short = Short.MIN_VALUE

    const val FLAG_EXECUTION_MODE_UI: Short = 0x0001
    const val FLAG_EXECUTION_MODE_AUTO: Short = 0x0002

    const val BLOCK_SIZE = 8
    private val BLOCK = byteArrayOf(0x77, 0x01, 0x17, 0x7F, 0x12, 0x12)

    fun getHeaderFlags(file: File): Short {
        val fis = FileInputStream(file)
        val bytes = ByteArray(BLOCK_SIZE)
        if (fis.read(bytes) < BLOCK_SIZE) {
            return FLAG_INVALID_FILE
        }
        if (!isValidFile(bytes)) {
            return FLAG_INVALID_FILE
        }
        return (bytes[BLOCK.size].toShort() * 256 + bytes[BLOCK.size + 1]).toShort()
    }

    fun isValidFile(bytes: ByteArray): Boolean {
        for (i in 0 until BLOCK.size) {
            if (bytes[i] != BLOCK[i]) {
                return false
            }
        }
        return true
    }

    fun writeHeader(os: OutputStream, flags: Short = 0) {
        os.write(BLOCK)
        val byte6 = flags / 256
        val byte7 = flags % 256
        os.write(byte6)
        os.write(byte7)
    }


}