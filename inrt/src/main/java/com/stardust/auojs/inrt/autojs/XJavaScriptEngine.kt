package com.stardust.auojs.inrt.autojs

import android.content.Context
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine
import com.stardust.autojs.engine.encryption.ScriptEncryption
import com.stardust.autojs.script.EncryptedScriptFileHeader
import com.stardust.autojs.script.JavaScriptFileSource
import com.stardust.autojs.script.ScriptSource
import com.stardust.autojs.script.StringScriptSource
import com.stardust.pio.PFiles
import java.io.File
import java.security.GeneralSecurityException

class XJavaScriptEngine(context: Context) : LoopBasedJavaScriptEngine(context) {


    override fun execute(source: ScriptSource, callback: ExecuteCallback?) {
        if (source is JavaScriptFileSource) {
            try {
                execute(source.file)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        } else {
            super.execute(source, callback)
        }
    }

    private fun execute(file: File) {
        val bytes = PFiles.readBytes(file.path)
        try {
            super.execute(StringScriptSource(file.name, String(ScriptEncryption.decrypt(bytes, EncryptedScriptFileHeader.BLOCK_SIZE))))
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
    }

}