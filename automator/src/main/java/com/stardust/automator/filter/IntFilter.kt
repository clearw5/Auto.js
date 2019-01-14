package com.stardust.automator.filter

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/11/5.
 */

class IntFilter(private val mIntProperty: IntProperty, private val mValue: Int) : Filter {

    interface IntProperty {
        operator fun get(`object`: UiObject): Int
    }

    override fun filter(node: UiObject): Boolean {
        return mIntProperty[node] == mValue
    }

    override fun toString(): String {
        return mIntProperty.toString() + "(" + mValue + ")"
    }

    companion object {

        val DEPTH: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.depth()
            }

            override fun toString(): String {
                return "depth"
            }
        }

        val ROW: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.row()
            }

            override fun toString(): String {
                return "row"
            }
        }

        val ROW_COUNT: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.rowCount()
            }

            override fun toString(): String {
                return "rowCount"
            }
        }

        val ROW_SPAN: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.rowSpan()
            }

            override fun toString(): String {
                return "rowSpan"
            }
        }

        val COLUMN: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.column()
            }

            override fun toString(): String {
                return "column"
            }
        }

        val COLUMN_COUNT: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.columnCount()
            }

            override fun toString(): String {
                return "columnCount"
            }
        }

        val COLUMN_SPAN: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.columnSpan()
            }

            override fun toString(): String {
                return "columnSpan"
            }
        }

        val INDEX_IN_PARENT: IntProperty = object : IntProperty {
            override fun get(`object`: UiObject): Int {
                return `object`.indexInParent()
            }

            override fun toString(): String {
                return "indexInParent"
            }
        }
    }
}
