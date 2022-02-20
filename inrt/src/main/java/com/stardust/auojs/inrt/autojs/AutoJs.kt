package com.stardust.auojs.inrt.autojs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.stardust.app.GlobalAppContext
import com.stardust.auojs.inrt.LogActivity
import com.stardust.auojs.inrt.Pref
import com.stardust.auojs.inrt.R
import com.stardust.auojs.inrt.SettingsActivity
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.runtime.api.AppUtils
import com.stardust.autojs.runtime.exception.ScriptException
import com.stardust.autojs.runtime.exception.ScriptInterruptedException
import com.stardust.autojs.script.JavaScriptSource
import com.stardust.view.accessibility.AccessibilityService
import com.stardust.view.accessibility.AccessibilityServiceUtils


/**
 * Created by Stardust on 2017/4/2.
 */

class AutoJs private constructor(application: Application) : com.stardust.autojs.AutoJs(application) {

    private val currentAccessibilityService = com.taobao.idlefish.AccessibilityService::class.java

    init {
        scriptEngineService.registerGlobalScriptExecutionListener(ScriptExecutionGlobalListener())
    }

    override fun createAppUtils(context: Context): AppUtils {
        return AppUtils(context, context.packageName + ".fileprovider")
    }

    private fun checkAccessibilityServiceEnabled(): String? {
        if (AccessibilityService.instance != null) {
            return null
        }
        var errorMessage: String? = null
        if (AccessibilityServiceUtils.isAccessibilityServiceEnabled(application, currentAccessibilityService)) {
            if (Pref.haveAdbPermission(application)) {
                // 尝试通过ADB权限移除无障碍权限 再重新通过ADB获取权限
                if (AccessibilityServiceTool.disableAccessibilityServiceByAdb(application, currentAccessibilityService)
                    && AccessibilityServiceTool.enableAccessibilityServiceByAdbAndWaitFor(application, 2000, currentAccessibilityService)) {
                    // 重新获取无障碍权限成功
                    return null
                }
            }
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running)
        } else {
            if (Pref.haveAdbPermission(application)) {
                // 尝试ADB授权
                if (AccessibilityServiceTool.enableAccessibilityServiceByAdbAndWaitFor(application, 2000, currentAccessibilityService)) {
                    return null
                }
            }
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(application, 2000, currentAccessibilityService)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout)
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission)
            }
        }
        return errorMessage
    }

    override fun ensureAccessibilityServiceEnabled() {
        val errorMessage: String? = checkAccessibilityServiceEnabled()
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting()
            throw ScriptException(errorMessage)
        }
    }

    override fun waitForAccessibilityServiceEnabled() {
        val errorMessage: String? = checkAccessibilityServiceEnabled()
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting()
            if (!AccessibilityService.waitForEnabled(-1)) {
                throw ScriptInterruptedException()
            }
        }
    }

    override fun initScriptEngineManager() {
        super.initScriptEngineManager()
        scriptEngineManager.registerEngine(JavaScriptSource.ENGINE) {
            val engine = XJavaScriptEngine(application)
            engine.runtime = createRuntime()
            engine
        }
    }

    override fun createRuntime(): ScriptRuntime {
        val runtime = super.createRuntime()
        runtime.putProperty("class.settings", SettingsActivity::class.java)
        runtime.putProperty("class.console", LogActivity::class.java)
        return runtime
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: AutoJs
            private set

        fun initInstance(application: Application) {
            instance = AutoJs(application)
        }
    }
}
