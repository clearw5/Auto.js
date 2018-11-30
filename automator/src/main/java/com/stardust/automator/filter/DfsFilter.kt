package com.stardust.automator.filter

import com.stardust.automator.UiObject

import java.util.ArrayList

/**
 * Created by Stardust on 2017/3/9.
 */

abstract class DfsFilter : ListFilter(), Filter {

    override fun filter(nodes: List<UiObject>): List<UiObject> {
        val list = ArrayList<UiObject>()
        for (node in nodes) {
            if (isIncluded(node)) {
                list.add(node)
            }
            filterChildren(node, list)
        }
        return list
    }

    override fun filter(node: UiObject): List<UiObject> {
        val list = ArrayList<UiObject>()
        if (isIncluded(node)) {
            list.add(node)
        }
        filterChildren(node, list)
        return list
    }

    private fun filterChildren(parent: UiObject, list: MutableList<UiObject>) {
        for (i in 0 until parent.childCount) {
            val child = parent.child(i) ?: continue
            val included = isIncluded(child)
            if (included) {
                list.add(child)
            }
            filterChildren(child, list)
            if (!included) {
                child.recycle()
            }
        }
    }

    protected abstract fun isIncluded(nodeInfo: UiObject): Boolean
}
