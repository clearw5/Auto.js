package com.stardust.autojs.engine

import android.util.Log
import android.view.View
import com.stardust.autojs.core.ui.ViewExtras
import com.stardust.autojs.engine.module.AssetAndUrlModuleSourceProvider
import com.stardust.autojs.execution.ExecutionConfig
import com.stardust.autojs.project.ScriptConfig
import com.stardust.autojs.rhino.RhinoAndroidHelper
import com.stardust.autojs.rhino.TopLevelScope
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.runtime.exception.ScriptInterruptedException
import com.stardust.autojs.script.JavaScriptSource
import com.stardust.automator.UiObjectCollection
import com.stardust.pio.UncheckedIOException
import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.commonjs.module.RequireBuilder
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.lang.ref.WeakReference
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Stardust on 2017/4/2.
 */

open class RhinoJavaScriptEngine(private val mAndroidContext: android.content.Context) :
    JavaScriptEngine() {

    private val context: WeakReference<Context>
    private val mScriptable: WeakReference<TopLevelScope>
    private var unsealed: Boolean = false
    lateinit var threadRef: WeakReference<Thread>

    private val initScript: Script
        get() {
            return sInitScript ?: {
                try {
                    val reader = InputStreamReader(mAndroidContext.assets.open("init.js"))
                    val script = context.get()?.compileReader(reader, SOURCE_NAME_INIT, 1, null)
                    sInitScript = script
                    script
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }()!!
        }

    fun getContext(): Context? {
        return context.get()
    }

    fun getThread(): Thread? {
        return threadRef.get()
    }

    val scriptable: Scriptable
        get() = mScriptable.get()!!

    init {
        this.context = WeakReference(enterContext())
        mScriptable = this.context.get()?.let { createScope(it) }!!
        this.context.get()?.seal(mScriptable)
    }

    override fun put(name: String, value: Any?) {
        ScriptableObject.putProperty(
            mScriptable.get(),
            name,
            Context.javaToJS(value, mScriptable.get())
        )
    }

    override fun setRuntime(runtime: ScriptRuntime) {
        super.setRuntime(runtime)
        runtime.topLevelScope = mScriptable.get()
    }

    public override fun doExecution(source: JavaScriptSource): Any? {
        var reader = source.nonNullScriptReader
        try {
            reader = preprocess(reader)
            val script = context.get()?.compileReader(reader, source.toString(), 1, null)
            return if (hasFeature(ScriptConfig.FEATURE_CONTINUATION)) {
                context.get()?.executeScriptWithContinuations(script, mScriptable.get())
            } else {
                script?.exec(context.get(), mScriptable.get())
            }
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }

    }

    fun hasFeature(feature: String): Boolean {
        val config = getTag(ExecutionConfig.tag) as ExecutionConfig?
        return config != null && config.scriptConfig.hasFeature(feature)
    }

    @Throws(IOException::class)
    protected fun preprocess(script: Reader): Reader {
        return script
    }

    override fun forceStop() {
        Log.d(LOG_TAG, "forceStop: interrupt Thread: ${threadRef.get()}")
        threadRef.get()?.interrupt()
    }


    @Synchronized
    override fun destroy() {
        var destroySuccess = false;
        try {
            super.destroy()
            Log.d(LOG_TAG, "on destroy")
            destroySuccess = true;
        } finally {
            if (destroySuccess)
                Log.d(LOG_TAG, "destroy execute success")
            else
                Log.d(LOG_TAG, "destroy execute failed")
            sContextEngineMap.remove(context.get())
            Context.exit()
        }
    }

    override fun init() {
        threadRef = WeakReference(Thread.currentThread())
        ScriptableObject.putProperty(mScriptable.get(), "__engine__", this)
        mScriptable.get()?.let { context.get()?.let { ctx -> initRequireBuilder(ctx, it) } }
        try {
            context.get()?.executeScriptWithContinuations(initScript, mScriptable.get())
        } catch (e: IllegalArgumentException) {
            if ("Script argument was not a script or was not created by interpreted mode " == e.message) {
                initScript.exec(context.get(), mScriptable.get())
            } else {
                throw e
            }
        }
    }

    internal fun initRequireBuilder(context: Context, scope: Scriptable) {
        val provider = AssetAndUrlModuleSourceProvider(
            mAndroidContext, MODULES_PATH,
            listOf<URI>(File("/").toURI())
        )
        RequireBuilder()
            .setModuleScriptProvider(SoftCachingModuleScriptProvider(provider))
            .setSandboxed(true)
            .createRequire(context, scope)
            .install(scope)

    }

    protected fun createScope(context: Context): WeakReference<TopLevelScope> {
        val topLevelScope = TopLevelScope()
        topLevelScope.initStandardObjects(context, false)
        if (unsealed) {
            // FIXME 会导致无法正常被回收 存在内存泄露可能
            topLevelScope.setNoRecycle()
        }
        return WeakReference(topLevelScope)
    }

    fun enterContext(): Context {
        val context = RhinoAndroidHelper(mAndroidContext).enterContext()
        setupContext(context)
        sContextEngineMap[context] = this
        return context
    }

    fun exitContext(context: Context) {
        sContextEngineMap.remove(context)
        try {
            Context.exit()
        } catch (e: Exception) {
            // do nothing
        }
    }

    protected fun setupContext(context: Context) {
        // FIXME 同时跑多个UI脚本时会共用同一个Context 此时的WrapFactory会被覆盖
        if (context.isSealed) {
            unsealed = true
            context.unseal(sContextEngineMap[context]?.mScriptable)
        } else {
            context.optimizationLevel = -1
            context.languageVersion = Context.VERSION_ES6
            context.locale = Locale.getDefault()
            context.wrapFactory = WrapFactory()
        }
    }

    private inner class WrapFactory : org.mozilla.javascript.WrapFactory() {

        override fun wrap(cx: Context, scope: Scriptable, obj: Any?, staticType: Class<*>?): Any? {
            return when {
                obj is String -> runtime.bridges.toString(obj.toString())
                staticType == UiObjectCollection::class.java -> runtime.bridges.asArray(obj)
                else -> {
                    if (scope is TopLevelScope) {
                        if (scope.isRecycled) {
                            throw ScriptInterruptedException()
                        }
                    }
                    super.wrap(cx, scope, obj, staticType)
                }
            }
        }

        override fun wrapAsJavaObject(
            cx: Context?,
            scope: Scriptable,
            javaObject: Any?,
            staticType: Class<*>?
        ): Scriptable? {
            //Log.d(LOG_TAG, "wrapAsJavaObject: java = " + javaObject + ", result = " + result + ", scope = " + scope);
            return if (javaObject is View) {
                ViewExtras.getNativeView(scope, javaObject, staticType, runtime)
            } else {
                super.wrapAsJavaObject(cx, scope, javaObject, staticType)
            }
        }

    }

    companion object {

        val SOURCE_NAME_INIT = "<init>"

        private val LOG_TAG = "RhinoJavaScriptEngine"

        private val MODULES_PATH = "modules"
        private var sInitScript: Script? = null
        private val sContextEngineMap = ConcurrentHashMap<Context, RhinoJavaScriptEngine>()


        fun getEngineOfContext(context: Context): RhinoJavaScriptEngine? {
            return sContextEngineMap[context]
        }
    }


}
