package com.stardust.auojs.inrt.autojs

import android.content.Context
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine
import com.stardust.autojs.script.JavaScriptFileSource
import com.stardust.autojs.script.ScriptSource
import com.stardust.autojs.script.StringScriptSource
import com.stardust.pio.PFiles
import com.stardust.util.AdvancedEncryptionStandard
import java.io.File

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
        //val key = AdvancedEncryptionStandard(mKey.toByteArray(), mInitVector).decrypt(bytes, 0, 16)
        val source = AdvancedEncryptionStandard(mKey.toByteArray(), mInitVector).decrypt(bytes)
        super.execute(StringScriptSource(file.name, String(source)))
    }

    fun setKey(key: String, initVector: String) {
        mKey = key
        mInitVector = initVector
    }


}