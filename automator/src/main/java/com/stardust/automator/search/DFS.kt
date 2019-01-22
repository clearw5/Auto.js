package com.stardust.automator.search

import com.stardust.automator.UiObject
import com.stardust.automator.filter.Filter
import java.util.*

import kotlin.collections.ArrayList

/**
 * Created by Stardust on 2017/3/9.
 */

object DFS : SearchAlgorithm {

    override fun search(root: UiObject, filter: Filter, limit: Int): ArrayList<UiObject> {
        val result = ArrayList<UiObject>()
        val stack = LinkedList<UiObject>()
        stack.push(root)
        while (stack.isNotEmpty()) {
            val parent = stack.pop()
            for (i in parent.childCount - 1 downTo 0) {
                val child = parent.child(i) ?: continue
                stack.push(child)
            }
            if (filter.filter(parent)) {
                result.add(parent)
                if (result.size >= limit) {
                    break
                }
            } else {
                if (parent !== root) {
                    parent.recycle()
                }
            }
        }
        return result
    }
}
