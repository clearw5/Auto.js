package com.stardust.view.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils

import java.util.Locale


/**
 * Created by Stardust on 2017/1/26.
 */

object AccessibilityServiceUtils {

    fun goToAccessibilitySetting(context: Context) {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(context, accessibilityService)

        val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)

            if (enabledService != null && enabledService == expectedComponentName)
                return true
        }

        return false
    }


}
