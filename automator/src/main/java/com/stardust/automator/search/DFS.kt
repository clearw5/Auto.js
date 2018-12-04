package com.stardust.automator.search

import com.stardust.automator.UiObject
import com.stardust.automator.filter.Filter

import kotlin.collections.ArrayList

/**
 * Created by Stardust on 2017/3/9.
 */

object DFS : SearchAlgorithm {

    override fun search(root: UiObject, filter: Filter, limit: Int): ArrayList<UiObject> {
        val list = ArrayList<UiObject>()
        if (filter.filter(root)) {
            list.add(root)
            if (list.size >= limit) {
                return list
            }
        }
        searchChildren(root, list, filter, limit)
        return list
    }

    private fun searchChildren(parent: UiObject, list: MutableList<UiObject>, filter: Filter, limit: Int) {
        for (i in 0 until parent.childCount) {
            val child = parent.child(i) ?: continue
            val isTarget = filter.filter(child)
            if (isTarget) {
                list.add(child)
                if (list.size >= limit) {
                    break
                }
            }
            searchChildren(child, list, filter, limit)
            if (!isTarget) {
                child.recycle()
            }
        }
    }

}
