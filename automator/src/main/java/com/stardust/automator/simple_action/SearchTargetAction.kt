package com.stardust.automator.simple_action

import android.graphics.BitmapFactory

import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/1/27.
 */

abstract class SearchTargetAction(val action: Int, filter: FilterAction.Filter) : FilterAction(filter) {

    override fun perform(nodes: List<UiObject>): Boolean {
        var performed = false
        for (node in nodes) {
            val targetNode = searchTarget(node)
            if (targetNode != null && performAction(targetNode)) {
                performed = true
            }
        }
        return performed
    }

    protected open fun performAction(node: UiObject): Boolean {
        return node.performAction(action)
    }

    open fun searchTarget(node: UiObject?): UiObject? {
        return node
    }

    override fun toString(): String {
        return "SearchTargetAction{" +
                "mAction=" + action + ", " +
                super.toString() +
                "}"
    }
}
