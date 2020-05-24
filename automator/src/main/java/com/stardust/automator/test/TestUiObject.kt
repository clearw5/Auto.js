package com.stardust.automator.test

import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import com.stardust.automator.UiObject
import java.util.*

/**
 * Created by Stardust on 2017/5/5.
 */

class TestUiObject @JvmOverloads constructor(private val mChildCount: Int = Math.max(0, random.nextInt(6) - 2)) : UiObject(null) {

    private val mHashCode = random.nextInt()
    private var mRecycled = false

    init {
        count++
        max = Math.max(max, count)
    }

    override fun child(i: Int): UiObject? {
        return TestUiObject()
    }

    override fun parent(): UiObject? {
        return TestUiObject()
    }

    override fun getChildCount(): Int {
        return mChildCount
    }

    override fun isScrollable(): Boolean {
        return random.nextInt(4) == 0
    }

    override fun isClickable(): Boolean {
        return random.nextBoolean()
    }

    override fun getBoundsInScreen(outBounds: Rect) {
        val left = random.nextInt(1080)
        val top = random.nextInt(1920)
        val right = random.nextInt(1080 - left) + left
        val bottom = random.nextInt(1920 - top) + top
        outBounds.set(left, top, right, bottom)
    }

    override fun performAction(action: Int, bundle: Bundle): Boolean {
        return random.nextBoolean()
    }

    override fun performAction(action: Int): Boolean {
        return random.nextBoolean()
    }

    override fun recycle() {
        if (mRecycled) {
            throw IllegalStateException()
        }
        mRecycled = true
        count--
    }

    override fun toString(): String {
        return "UiObject@" + Integer.toHexString(hashCode())
    }

    override fun hashCode(): Int {
        return mHashCode
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    companion object {

        var count = 0
        var max = 0
        private val random = Random()
    }
}
