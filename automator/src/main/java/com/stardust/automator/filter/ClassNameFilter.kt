package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

object ClassNameFilter {

    private val CLASS_NAME_GETTER = object : KeyGetter {
        override fun getKey(nodeInfo: UiObject): String? {
            val charSequence = nodeInfo.className
            return charSequence?.toString()
        }

        override fun toString(): String {
            return "className"
        }
    }

    fun equals(text: String): ListFilter {
        var text = text
        if (!text.contains(".")) {
            text = "android.widget.$text"
        }
        return StringEqualsFilter(text, CLASS_NAME_GETTER)
    }

    fun contains(str: String): ListFilter {
        return StringContainsFilter(str, CLASS_NAME_GETTER)
    }

    fun startsWith(prefix: String): ListFilter {
        return StringStartsWithFilter(prefix, CLASS_NAME_GETTER)
    }

    fun endsWith(suffix: String): ListFilter {
        return StringEndsWithFilter(suffix, CLASS_NAME_GETTER)
    }

    fun matches(regex: String): ListFilter {
        return StringMatchesFilter(regex, CLASS_NAME_GETTER)
    }
}
