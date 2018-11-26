package com.stardust.automator.simple_action


import com.stardust.automator.UiObject

/**
 * Created by Stardust on 2017/1/27.
 */

class SearchUpTargetAction(action: Int, filter: FilterAction.Filter) : SearchTargetAction(action, filter) {
    private val mAble: Able = Able.ABLE_MAP.get(action)

    override fun searchTarget(n: UiObject?): UiObject? {
        var node = n
        var i = 0
        while (node != null && !mAble.isAble(node)) {
            i++
            if (i > LOOP_MAX) {
                return null
            }
            node = node.parent()
        }
        return node
    }

    override fun toString(): String {
        return "SearchUpTargetAction{" +
                "mAble=" + mAble + ", " +
                super.toString() +
                '}'.toString()
    }

    companion object {

        private val TAG = SearchUpTargetAction::class.java!!.getSimpleName()
        private val LOOP_MAX = 20
    }
}
