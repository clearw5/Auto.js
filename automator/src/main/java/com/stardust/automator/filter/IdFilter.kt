package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

object IdFilter {

    private val ID_GETTER = object : KeyGetter {

        override fun getKey(nodeInfo: UiObject): String? {
            return nodeInfo.viewIdResourceName
        }

        override fun toString(): String {
            return "id"
        }
    }

    fun equals(id: String): StringEqualsFilter {
        return StringEqualsFilter(id, ID_GETTER)
    }

    fun startsWith(prefix: String): StringStartsWithFilter {
        return StringStartsWithFilter(prefix, ID_GETTER)
    }

    fun endsWith(suffix: String): StringEndsWithFilter {
        return StringEndsWithFilter(suffix, ID_GETTER)
    }

    fun contains(contains: String): StringContainsFilter {
        return StringContainsFilter(contains, ID_GETTER)
    }

    fun matches(regex: String): StringMatchesFilter {
        return StringMatchesFilter(regex, ID_GETTER)
    }

}
