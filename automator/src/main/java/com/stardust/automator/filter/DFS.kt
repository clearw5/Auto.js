package com.stardust.automator.filter

import com.stardust.automator.UiObject

import java.util.ArrayList

/**
 * Created by Stardust on 2017/3/9.
 */

class DFS(val filter: Filter, val limit: Int = Int.MAX_VALUE) {

    fun search(node: UiObject): List<UiObject> {
        val list = ArrayList<UiObject>()
        if (filter.filter(node)) {
            list.add(node)
            if (list.size >= limit) {
                return list
            }
        }
        filterChildren(node, list)
        return list
    }

    private fun filterChildren(parent: UiObject, list: MutableList<UiObject>) {
        for (i in 0 until parent.childCount) {
            val child = parent.child(i) ?: continue
            val included = filter.filter(child)
            if (included) {
                list.add(child)
                if (list.size >= limit) {
                    break
                }
            }
            filterChildren(child, list)
            if (!included) {
                child.recycle()
            }
        }
    }

}
