package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

object ClassNameFilters {

    private val CLASS_NAME_GETTER = object : KeyGetter {
        override fun getKey(nodeInfo: UiObject): String? {
            val charSequence = nodeInfo.className
            return charSequence?.toString()
        }

        override fun toString(): String {
            return "className"
        }
    }

    fun equals(text: String): Filter {
        var className = text
        if (!className.contains(".")) {
            className = "android.widget.$className"
        }
        return StringEqualsFilter(className, CLASS_NAME_GETTER)
    }

    fun contains(str: String): Filter {
        return StringContainsFilter(str, CLASS_NAME_GETTER)
    }

    fun startsWith(prefix: String): Filter {
        return StringStartsWithFilter(prefix, CLASS_NAME_GETTER)
    }

    fun endsWith(suffix: String): Filter {
        return StringEndsWithFilter(suffix, CLASS_NAME_GETTER)
    }

    fun matches(regex: String): Filter {
        return StringMatchesFilter(regex, CLASS_NAME_GETTER)
    }
}
