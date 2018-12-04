package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

class StringEqualsFilter(private val mValue: String, private val mKeyGetter: KeyGetter) : Filter {

    override fun filter(node: UiObject): Boolean {
        val key = mKeyGetter.getKey(node)
        return if (key != null) {
            key == mValue
        } else false
    }

    override fun toString(): String {
        return mKeyGetter.toString() + "(\"" + mValue + "\")"
    }
}
