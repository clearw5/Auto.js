package com.stardust.automator.simple_action

import com.stardust.automator.UiObject

import java.util.ArrayList

/**
 * Created by Stardust on 2017/2/12.
 */
class ScrollAction(private val mAction: Int, private val mIndex: Int) : SimpleAction() {

    override fun perform(root: UiObject): Boolean {
        val scrollableNodes = findScrollableNodes(root)
        val result = mIndex < scrollableNodes.size && scrollableNodes[mIndex].performAction(mAction)
        recycle(scrollableNodes, root)
        return result
    }

    private fun recycle(list: List<UiObject>, root: UiObject) {
        for (nodeInfo in list) {
            if (nodeInfo !== root)
                nodeInfo.recycle()
        }
    }

    private fun findScrollableNodes(root: UiObject?): List<UiObject> {
        val list = ArrayList<UiObject>()
        if (root != null) {
            findScrollableNodes(root, list)
            if (root.isScrollable) {
                list.add(root)
            }
        }
        return list
    }

    private fun findScrollableNodes(node: UiObject?, list: MutableList<UiObject>) {
        if (node == null) {
            return
        }
        for (i in 0 until node.childCount) {
            val child = node.child(i) ?: continue
            findScrollableNodes(child, list)
            if (child.isScrollable) {
                list.add(child)
            } else {
                child.recycle()
            }
        }
    }
}
