package com.stardust.automator.simple_action

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/1/27.
 */

class DepthFirstSearchTargetAction(action: Int, filter: FilterAction.Filter) : SearchTargetAction(action, filter) {

    private val mAble: Able = Able.ABLE_MAP.get(action)

    override fun searchTarget(node: UiObject?): UiObject? {
        if (node == null)
            return null
        if (mAble.isAble(node))
            return node
        for (i in 0 until node.childCount) {
            val child = node.child(i) ?: continue
            val targetNode = searchTarget(child)
            if (targetNode != null)
                return targetNode
        }
        return null
    }


}
