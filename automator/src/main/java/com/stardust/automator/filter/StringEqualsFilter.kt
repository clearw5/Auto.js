package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

class StringEqualsFilter(private val mValue: String, private val mKeyGetter: KeyGetter) : DfsFilter() {

    override fun isIncluded(nodeInfo: UiObject): Boolean {
        val key = mKeyGetter.getKey(nodeInfo)
        return if (key != null) {
            key == mValue
        } else false
    }

    override fun toString(): String {
        return mKeyGetter.toString() + "(\"" + mValue + "\")"
    }
}
