package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

object PackageNameFilter {

    private val PACKAGE_NAME_GETTER = object : KeyGetter {
        override fun getKey(nodeInfo: UiObject): String? {
            val charSequence = nodeInfo.packageName
            return charSequence?.toString()
        }

        override fun toString(): String {
            return "packageName"
        }
    }

    fun equals(text: String): ListFilter {
        return StringEqualsFilter(text, PACKAGE_NAME_GETTER)
    }

    fun contains(str: String): ListFilter {
        return StringContainsFilter(str, PACKAGE_NAME_GETTER)
    }

    fun startsWith(prefix: String): ListFilter {
        return StringStartsWithFilter(prefix, PACKAGE_NAME_GETTER)
    }

    fun endsWith(suffix: String): ListFilter {
        return StringEndsWithFilter(suffix, PACKAGE_NAME_GETTER)
    }

    fun matches(regex: String): ListFilter {
        return StringMatchesFilter(regex, PACKAGE_NAME_GETTER)
    }
}
