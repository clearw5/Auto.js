package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

interface KeyGetter {

    fun getKey(nodeInfo: UiObject): String?
}
