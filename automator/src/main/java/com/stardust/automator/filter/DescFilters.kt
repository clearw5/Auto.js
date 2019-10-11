package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

object DescFilters {

    private val DESC_GETTER = object : KeyGetter {
        override fun getKey(nodeInfo: UiObject): String? {
            val charSequence = nodeInfo.contentDescription
            return charSequence?.toString()
        }

        override fun toString(): String {
            return "desc"
        }
    }

    fun equals(text: String): Filter {
        return StringEqualsFilter(text, DESC_GETTER)
    }

    fun contains(str: String): Filter {
        return StringContainsFilter(str, DESC_GETTER)
    }

    fun startsWith(prefix: String): Filter {
        return StringStartsWithFilter(prefix, DESC_GETTER)
    }

    fun endsWith(suffix: String): Filter {
        return StringEndsWithFilter(suffix, DESC_GETTER)
    }

    fun matches(regex: String): Filter {
        return StringMatchesFilter(regex, DESC_GETTER)
    }
}
