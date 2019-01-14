package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

object TextFilters {

    private val TEXT_GETTER = object : KeyGetter {
        override fun getKey(nodeInfo: UiObject): String? {
            val charSequence = nodeInfo.text
            return charSequence?.toString()
        }

        override fun toString(): String {
            return "text"
        }
    }

    fun equals(text: String): Filter {
        return StringEqualsFilter(text, TEXT_GETTER)
    }

    fun contains(str: String): Filter {
        return StringContainsFilter(str, TEXT_GETTER)
    }

    fun startsWith(prefix: String): Filter {
        return StringStartsWithFilter(prefix, TEXT_GETTER)
    }

    fun endsWith(suffix: String): Filter {
        return StringEndsWithFilter(suffix, TEXT_GETTER)
    }

    fun matches(regex: String): Filter {
        return StringMatchesFilter(regex, TEXT_GETTER)
    }
}
