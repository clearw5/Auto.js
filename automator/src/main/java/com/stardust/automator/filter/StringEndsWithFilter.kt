package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

class StringEndsWithFilter(private val mSuffix: String, private val mKeyGetter: KeyGetter) : DfsFilter() {

    override fun isIncluded(nodeInfo: UiObject): Boolean {
        val key = mKeyGetter.getKey(nodeInfo)
        return key != null && key.endsWith(mSuffix)
    }

    override fun toString(): String {
        return mKeyGetter.toString() + "EndsWith(\"" + mSuffix + "\")"
    }
}
