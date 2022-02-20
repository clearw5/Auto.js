package com.stardust.auojs.inrt.autojs

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.util.ProcessShell
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Stardust on 2017/7/1.
 */

object AccessibilityServiceTool {

    private val cmd = "enabled=$(settings get secure enabled_accessibility_services)\n" +
            "pkg=%s\n" +
            "if [[ \$enabled == *\$pkg* ]]\n" +
            "then\n" +
            "echo already_enabled\n" +
            "else\n" +
            "enabled=\$pkg:\$enabled\n" +
            "settings put secure enabled_accessibility_services \$enabled\n" +
            "fi"

    private val SERVICE_PATTERN = Pattern.compile("^(((\\w+\\.)+\\w+)[/]?){2}$")

    fun enableAccessibilityServiceByRoot(context: Context, accessibilityService: Class<out AccessibilityService>): Boolean {
        val serviceName = context.packageName + "/" + accessibilityService.name
        return try {
            TextUtils.isEmpty(ProcessShell.execCommand(String.format(Locale.getDefault(), cmd, serviceName), true).error)
        } catch (ignored: Exception) {
            false
        }

    }

    fun enableAccessibilityServiceByRootAndWaitFor(context: Context, timeOut: Long, accessibilityService: Class<out AccessibilityService>): Boolean {
        if (enableAccessibilityServiceByRoot(context, accessibilityService)) {
            return com.stardust.view.accessibility.AccessibilityService.waitForEnabled(timeOut)
        }
        return false
    }

    fun goToAccessibilitySetting() {
        GlobalAppContext.get().startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun enableAccessibilityServiceByAdbAndWaitFor(context: Context, timeOut: Long, accessibilityService: Class<out AccessibilityService>): Boolean {
        if (enableAccessibilityServiceByAdb(context, accessibilityService)) {
            return com.stardust.view.accessibility.AccessibilityService.waitForEnabled(timeOut)
        }
        return false
    }

    fun enableAccessibilityServiceByAdb(context: Context, accessibilityService: Class<out AccessibilityService>): Boolean {
        // 尝试自动设置无障碍权限，需要ADB授权 adb shell pm grant ${BuildConfig.APPLICATION_ID} android.permission.WRITE_SECURE_SETTINGS
        try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val requiredService: String =
                context.packageName + "/" + accessibilityService.name
            val services = "$enabledServices:$requiredService"
            val serviceInfo = services.split(":").toTypedArray()
            val sb = StringBuilder()
            for (service in serviceInfo) {
                if (SERVICE_PATTERN.matcher(service).find()) {
                    sb.append(service).append(":")
                }
            }
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                sb.toString()
            )
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                "1"
            )
            return true
        } catch (ignored: Exception) {
            return false
        }
    }

    fun disableAccessibilityServiceByAdb(context: Context, accessibilityService: Class<out AccessibilityService>): Boolean {
        // 尝试自动设置无障碍权限，需要ADB授权 adb shell pm grant ${BuildConfig.APPLICATION_ID} android.permission.WRITE_SECURE_SETTINGS
        try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val requiredService: String =
                context.packageName + "/" + accessibilityService.name
            val services = enabledServices.replace(requiredService, "")
            val serviceInfo = services.split(":").toTypedArray()
            val sb = StringBuilder()
            for (service in serviceInfo) {
                if (SERVICE_PATTERN.matcher(service).find()) {
                    sb.append(service).append(":")
                }
            }
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                sb.toString()
            )
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                "1"
            )
            return true
        } catch (ignored: Exception) {
            return false
        }
    }
}
