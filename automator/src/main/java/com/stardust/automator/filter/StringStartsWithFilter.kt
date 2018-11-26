package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

class StringStartsWithFilter(private val mPrefix: String, private val mKeyGetter: KeyGetter) : DfsFilter() {

    override fun isIncluded(nodeInfo: UiObject): Boolean {
        val key = mKeyGetter.getKey(nodeInfo)
        return key != null && key.startsWith(mPrefix)
    }

    override fun toString(): String {
        return mKeyGetter.toString() + "StartsWith(\"" + mPrefix + "\")"
    }
}
