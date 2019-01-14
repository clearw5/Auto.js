package com.stardust.automator.search

import com.stardust.automator.UiObject
import com.stardust.automator.filter.Filter
import java.util.*
import kotlin.collections.ArrayList

object BFS : SearchAlgorithm {

    override fun search(root: UiObject, filter: Filter, limit: Int): ArrayList<UiObject> {
        val result = ArrayList<UiObject>()
        val queue = ArrayDeque<UiObject>()
        queue.add(root)
        while (!queue.isEmpty()) {
            val top = queue.poll()
            val isTarget = filter.filter(top)
            if (isTarget) {
                result.add(top)
                if (result.size > limit) {
                    return result
                }
            }
            for (i in 0 until top.childCount) {
                queue.add(top.child(i) ?: continue)
            }
            if (!isTarget && top !== root) {
                top.recycle()
            }
        }
        return result
    }
}