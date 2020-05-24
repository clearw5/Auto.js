package com.stardust.automator.simple_action

import android.graphics.Rect
import com.stardust.automator.UiObject
import java.util.*

/**
 * Created by Stardust on 2017/1/27.
 */

abstract class FilterAction(private val mFilter: Filter) : SimpleAction() {

    abstract fun perform(nodes: List<UiObject>): Boolean

    interface Filter {

        fun filter(root: UiObject): List<UiObject>
    }

    class TextFilter(private var mText: String, internal var mIndex: Int) : Filter {

        override fun filter(root: UiObject): List<UiObject> {
            val list = root.findByText(mText)
            if (mIndex == -1)
                return list
            return if (mIndex >= list.size) emptyList() else listOf(list[mIndex])
        }

        override fun toString(): String {
            return "TextFilter{" +
                    "mText='" + mText + '\''.toString() +
                    ", mIndex=" + mIndex +
                    '}'.toString()
        }
    }

    class BoundsFilter(private var mBoundsInScreen: Rect) : Filter {

        override fun filter(root: UiObject): List<UiObject> {
            val list = ArrayList<UiObject>()
            findAccessibilityNodeInfosByBounds(root, list)
            return list
        }

        private fun findAccessibilityNodeInfosByBounds(root: UiObject?, list: MutableList<UiObject>) {
            if (root == null)
                return
            val rect = Rect()
            root.getBoundsInScreen(rect)
            if (rect == mBoundsInScreen) {
                list.add(root)
            }
            val oldSize = list.size
            for (i in 0 until root.childCount) {
                val child = root.child(i) ?: continue
                findAccessibilityNodeInfosByBounds(child, list)
            }
            if (oldSize == list.size && rect.contains(mBoundsInScreen)) {
                list.add(root)
            }
        }

        override fun toString(): String {
            return "BoundsFilter{" +
                    "mBoundsInScreen=" + mBoundsInScreen +
                    '}'.toString()
        }
    }

    class EditableFilter(private val mIndex: Int) : Filter {

        override fun filter(root: UiObject): List<UiObject> {
            val editableList = findEditable(root)
            if (mIndex == -1)
                return editableList
            return if (mIndex >= editableList.size) emptyList() else listOf(editableList[mIndex])
        }

        override fun toString(): String {
            return "EditableFilter{" +
                    "mIndex=" + mIndex +
                    '}'.toString()
        }

        companion object {

            fun findEditable(root: UiObject?): List<UiObject> {
                if (root == null) {
                    return emptyList()
                }
                if (root.isEditable) {
                    return listOf(root)
                }
                val list = LinkedList<UiObject>()
                for (i in 0 until root.childCount) {
                    list.addAll(findEditable(root.child(i)))
                }
                return list
            }
        }
    }

    class IdFilter(private val mId: String) : Filter {

        override fun filter(root: UiObject): List<UiObject> {
            return root.findByViewId(mId)
        }

        override fun toString(): String {
            return "IdFilter{" +
                    "mId='" + mId + '\''.toString() +
                    '}'.toString()
        }
    }

    override fun perform(root: UiObject): Boolean {
        val list = mFilter.filter(root)
        return perform(list)
    }


    class SimpleFilterAction(private val mAction: Int, filter: Filter) : FilterAction(filter) {

        override fun perform(nodes: List<UiObject>): Boolean {
            if (nodes.isEmpty())
                return false
            var succeed = true
            for (nodeInfo in nodes) {
                if (!nodeInfo.performAction(mAction)) {
                    succeed = false
                }
            }
            return succeed
        }
    }

    override fun toString(): String {
        return "FilterAction{" +
                "mFilter=" + mFilter +
                '}'.toString()
    }
}
