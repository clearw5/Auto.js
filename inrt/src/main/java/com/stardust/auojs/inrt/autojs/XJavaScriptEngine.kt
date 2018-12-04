package com.stardust.auojs.inrt.autojs

import android.content.Context
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine
import com.stardust.autojs.script.JavaScriptFileSource
import com.stardust.autojs.script.ScriptSource
import com.stardust.autojs.script.StringScriptSource
import com.stardust.pio.PFiles
import com.stardust.util.AdvancedEncryptionStandard
import java.io.File
import java.lang.Exception
import java.lang.IllegalStateException
import java.security.GeneralSecurityException

class XJavaScriptEngine(context: Context) : LoopBasedJavaScriptEngine(context) {

    private var mKey = ""
    private var mInitVector = ""

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
            val source = AdvancedEncryptionStandard(mKey.toByteArray(), mInitVector).decrypt(bytes)
            super.execute(StringScriptSource(file.name, String(source)))
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
    }

    fun setKey(key: String, initVector: String) {
        mKey = key
        mInitVector = initVector
    }


}