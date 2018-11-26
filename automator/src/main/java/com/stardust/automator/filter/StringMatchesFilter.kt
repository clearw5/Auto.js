package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

class StringMatchesFilter internal constructor(private val mRegex: String, private val mKeyGetter: KeyGetter) : DfsFilter() {

    override fun isIncluded(nodeInfo: UiObject): Boolean {
        val key = mKeyGetter.getKey(nodeInfo)
        return key != null && key.matches(mRegex.toRegex())
    }

    override fun toString(): String {
        return mKeyGetter.toString() + "Matches(\"" + mRegex + "\")"
    }
}
