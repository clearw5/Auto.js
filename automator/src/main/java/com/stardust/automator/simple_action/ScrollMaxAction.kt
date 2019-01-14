package com.stardust.automator.simple_action

import android.graphics.Rect
import android.util.Log

import com.stardust.automator.UiObject

import java.util.HashSet

/**
 * Created by Stardust on 2017/1/27.
 */

class ScrollMaxAction(private val mScrollAction: Int) : SimpleAction() {
    private var mMaxScrollableNode: UiObject? = null
    private var mRootNode: UiObject? = null
    private val mRecycledMaxUiObjects = HashSet<UiObject>()

    override fun perform(root: UiObject): Boolean {
        reset()
        mRootNode = root
        findMaxScrollableNodeInfo(root)
        val result = mMaxScrollableNode != null && mMaxScrollableNode!!.performAction(mScrollAction)
        reset()
        return result
    }

    private fun reset() {
        mMaxScrollableNode?.let {
            if(it != mRootNode){
                it.recycle()
            }
        }
        mRootNode = null
        mMaxScrollableNode = mRootNode
        mRecycledMaxUiObjects.clear()
    }

    private fun findMaxScrollableNodeInfo(nodeInfo: UiObject?) {
        if (nodeInfo == null)
            return

        if (nodeInfo.isScrollable) {
            val maxScrollableNode = mMaxScrollableNode
            if (maxScrollableNode == null) {
                mMaxScrollableNode = nodeInfo
            } else if (getAreaInScreen(maxScrollableNode) < getAreaInScreen(nodeInfo)) {
                if (maxScrollableNode !== mRootNode) {
                    mRecycledMaxUiObjects.add(maxScrollableNode)
                    maxScrollableNode.recycle()
                }
                mMaxScrollableNode = nodeInfo
            }
        }
        for (i in 0 until nodeInfo.childCount) {
            val child = nodeInfo.child(i)
            if (child != null) {
                findMaxScrollableNodeInfo(child)
                if (mMaxScrollableNode !== child && !mRecycledMaxUiObjects.contains(child)) {
                    child.recycle()
                }
            }
        }
    }

    private fun getAreaInScreen(nodeInfo: UiObject): Long {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        val area = rect.width().toLong() * rect.height()
        Log.v(TAG, "area=$area")
        return area
    }

    companion object {

        private val TAG = ScrollMaxAction::class.java.simpleName
    }

}
