package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/3/9.
 */

class TextFilter private constructor(private val mText: String) : ListFilter.Default() {


    override fun filter(node: UiObject): List<UiObject> {
        return node.findByText(mText)
    }

    companion object {

        private val TEXT_GETTER = object : KeyGetter {
            override fun getKey(nodeInfo: UiObject): String? {
                val charSequence = nodeInfo.text
                return charSequence?.toString()
            }

            override fun toString(): String {
                return "text"
            }
        }

        fun equals(text: String): ListFilter {
            return StringEqualsFilter(text, TEXT_GETTER)
        }

        fun contains(str: String): ListFilter {
            return StringContainsFilter(str, TEXT_GETTER)
        }

        fun startsWith(prefix: String): ListFilter {
            return StringStartsWithFilter(prefix, TEXT_GETTER)
        }

        fun endsWith(suffix: String): ListFilter {
            return StringEndsWithFilter(suffix, TEXT_GETTER)
        }

        fun matches(regex: String): ListFilter {
            return StringMatchesFilter(regex, TEXT_GETTER)
        }
    }
}
