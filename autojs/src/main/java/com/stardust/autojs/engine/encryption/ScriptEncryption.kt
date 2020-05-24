package com.stardust.autojs.engine.encryption

import com.stardust.util.AdvancedEncryptionStandard

object ScriptEncryption {

    private var mKey = ""
    private var mInitVector = ""

    fun decrypt(bytes: ByteArray, start: Int = 0, end: Int = bytes.size): ByteArray {
        return AdvancedEncryptionStandard(mKey.toByteArray(), mInitVector).decrypt(bytes, start, end)
    }

}