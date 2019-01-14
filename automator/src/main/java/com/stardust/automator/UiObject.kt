package com.stardust.automator

import android.graphics.Rect
import android.os.Bundle
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

import com.stardust.view.accessibility.AccessibilityNodeInfoAllocator
import com.stardust.view.accessibility.AccessibilityNodeInfoHelper

import java.util.ArrayList
import java.util.Arrays

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CONTEXT_CLICK
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_TO_POSITION
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SHOW_ON_SCREEN

/**
 * Created by Stardust on 2017/3/9.
 */

open class UiObject(info: Any?, private val allocator: AccessibilityNodeInfoAllocator?, depth: Int, private val mIndexInParent: Int) : AccessibilityNodeInfoCompat(info) {


    private var mStackTrace = ""
    private var mDepth = 0

    val isHierarchically: Boolean
        get() = collectionInfo != null && collectionInfo.isHierarchical

    init {
        mDepth = depth
        if (DEBUG)
            mStackTrace = Arrays.toString(Thread.currentThread().stackTrace)

    }


    constructor(info: Any?, allocator: AccessibilityNodeInfoAllocator, indexInParent: Int) : this(info, allocator, 0, indexInParent) {}

    @JvmOverloads
    constructor(info: Any?, depth: Int = 0, indexInParent: Int = -1) : this(info, null, depth, indexInParent)

    open fun parent(): UiObject? {
        try {
            val parent = super.getParent() ?: return null
            return UiObject(parent.info, mDepth - 1, -1)
        } catch (e: IllegalStateException) {
            // FIXME: 2017/5/5
            return null
        }

    }

    open fun child(i: Int): UiObject? {
        try {
            val child = super.getChild(i) ?: return null
            return UiObject(child.info, mDepth + 1, i)
        } catch (e: IllegalStateException) {
            // FIXME: 2017/5/5
            return null
        }

    }

    fun indexInParent(): Int {
        return mIndexInParent
    }

    fun find(selector: UiGlobalSelector): UiObjectCollection {
        return selector.findOf(this)
    }

    fun findOne(selector: UiGlobalSelector): UiObject? {
        return selector.findOneOf(this)
    }

    fun children(): UiObjectCollection {
        val list = ArrayList<UiObject?>(childCount)
        for (i in 0 until childCount) {
            list.add(child(i))
        }
        return UiObjectCollection.of(list)
    }

    open fun childCount(): Int {
        return childCount
    }

    open fun bounds(): Rect {
        return AccessibilityNodeInfoHelper.getBoundsInScreen(this)
    }

    open fun boundsInParent(): Rect {
        return AccessibilityNodeInfoHelper.getBoundsInParent(this)
    }

    open fun drawingOrder(): Int {
        return drawingOrder
    }

    open fun id(): String? {
        return viewIdResourceName
    }

    open fun text(): String {
        val t = text
        return t?.toString() ?: ""
    }

    override fun getText(): CharSequence? {
        return if (isPassword) {
            ""
        } else super.getText()
    }

    open fun desc(): String? {
        val d = contentDescription
        return d?.toString()
    }

    open fun className(): String? {
        val d = className
        return d?.toString()
    }

    open fun packageName(): String? {
        val d = packageName
        return d?.toString()
    }

    open fun depth(): Int {
        return mDepth
    }

    fun performAction(action: Int, vararg arguments: ActionArgument): Boolean {
        val bundle = argumentsToBundle(arguments)
        return performAction(action, bundle)
    }

    override fun performAction(action: Int, bundle: Bundle): Boolean {
        return try {
            super.performAction(action, bundle)
        } catch (e: IllegalStateException) {
            // FIXME: 2017/5/5
            false
        }

    }

    override fun performAction(action: Int): Boolean {
        return try {
            super.performAction(action)
        } catch (e: IllegalStateException) {
            // FIXME: 2017/5/5
            false
        }
    }

    fun click(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
    }

    fun longClick(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_LONG_CLICK)
    }

    fun accessibilityFocus(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS)
    }

    fun clearAccessibilityFocus(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS)
    }

    fun focus(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_FOCUS)
    }

    fun clearFocus(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_CLEAR_FOCUS)
    }

    fun copy(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_COPY)
    }

    fun paste(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_PASTE)
    }

    fun select(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_SELECT)
    }

    fun cut(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_CUT)
    }

    fun collapse(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_COLLAPSE)
    }

    fun expand(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_EXPAND)
    }

    fun dismiss(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
    }

    fun show(): Boolean {
        return performAction(ACTION_SHOW_ON_SCREEN.id)
    }

    fun scrollForward(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD)
    }

    fun scrollBackward(): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD)
    }

    fun scrollUp(): Boolean {
        return performAction(ACTION_SCROLL_UP.id)
    }

    fun scrollDown(): Boolean {
        return performAction(ACTION_SCROLL_DOWN.id)
    }

    fun scrollLeft(): Boolean {
        return performAction(ACTION_SCROLL_LEFT.id)
    }

    fun scrollRight(): Boolean {
        return performAction(ACTION_SCROLL_RIGHT.id)
    }

    fun contextClick(): Boolean {
        return performAction(ACTION_CONTEXT_CLICK.id)
    }

    fun setSelection(s: Int, e: Int): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_SET_SELECTION,
                ActionArgument.IntActionArgument(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_START_INT, s),
                ActionArgument.IntActionArgument(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_END_INT, e))
    }

    fun setText(text: String): Boolean {
        return performAction(AccessibilityNodeInfoCompat.ACTION_SET_TEXT,
                ActionArgument.CharSequenceActionArgument(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text))
    }

    fun setProgress(value: Float): Boolean {
        return performAction(ACTION_SET_PROGRESS.id,
                ActionArgument.FloatActionArgument(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_PROGRESS_VALUE, value))
    }

    fun scrollTo(row: Int, column: Int): Boolean {
        return performAction(ACTION_SCROLL_TO_POSITION.id,
                ActionArgument.IntActionArgument(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_ROW_INT, row),
                ActionArgument.IntActionArgument(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_COLUMN_INT, column))
    }

    override fun getChild(index: Int): AccessibilityNodeInfoCompat {
        return if (allocator == null) super.getChild(index) else allocator.getChild(this, index)
    }

    override fun getParent(): AccessibilityNodeInfoCompat {
        return if (allocator == null) super.getParent() else allocator.getParent(this)
    }


    open fun checkable(): Boolean {
        return isCheckable
    }


    open fun checked(): Boolean {
        return isChecked
    }


    open fun focusable(): Boolean {
        return isFocusable
    }


    open fun focused(): Boolean {
        return isFocused
    }


    open fun visibleToUser(): Boolean {
        return isVisibleToUser
    }


    open fun accessibilityFocused(): Boolean {
        return isAccessibilityFocused
    }


    open fun selected(): Boolean {
        return isSelected
    }

    open fun clickable(): Boolean {
        return isClickable
    }


    open fun longClickable(): Boolean {
        return isLongClickable
    }


    open fun enabled(): Boolean {
        return isEnabled
    }


    fun password(): Boolean {
        return isPassword
    }


    open fun scrollable(): Boolean {
        return isScrollable
    }

    open fun row(): Int {
        return if (collectionItemInfo == null) -1 else collectionItemInfo.rowIndex
    }

    open fun column(): Int {
        return if (collectionItemInfo == null) -1 else collectionItemInfo.columnIndex
    }

    open fun rowSpan(): Int {
        return if (collectionItemInfo == null) -1 else collectionItemInfo.rowSpan
    }


    open fun columnSpan(): Int {
        return if (collectionItemInfo == null) -1 else collectionItemInfo.columnSpan
    }

    open fun rowCount(): Int {
        return if (collectionInfo == null) 0 else collectionInfo.rowCount
    }


    open fun columnCount(): Int {
        return if (collectionInfo == null) 0 else collectionInfo.columnCount
    }

    override fun findAccessibilityNodeInfosByText(text: String): List<AccessibilityNodeInfoCompat> {
        return allocator?.findAccessibilityNodeInfosByText(this, text)
                ?: super.findAccessibilityNodeInfosByText(text)
    }

    fun findByText(text: String): List<UiObject> {
        return UiGlobalSelector().textContains(text).findAndReturnList(this)
    }

    override fun findAccessibilityNodeInfosByViewId(viewId: String): List<AccessibilityNodeInfoCompat> {
        return allocator?.findAccessibilityNodeInfosByViewId(this, viewId)
                ?: super.findAccessibilityNodeInfosByViewId(viewId)
    }

    fun findByViewId(viewId: String): List<UiObject> {
        return UiGlobalSelector().id(viewId).findAndReturnList(this)
    }

    override fun recycle() {
        try {
            super.recycle()
        } catch (e: Exception) {
            Log.w(TAG, mStackTrace, e)
        }

    }

    companion object {

        val ACTION_APPEND_TEXT = 0x00200001

        private const val TAG = "UiObject"
        private const val DEBUG = false


        fun createRoot(root: AccessibilityNodeInfo): UiObject {
            return UiObject(root, null, 0, -1)
        }

        fun createRoot(root: AccessibilityNodeInfo, allocator: AccessibilityNodeInfoAllocator?): UiObject {
            return UiObject(root, allocator, 0, -1)
        }

        private fun argumentsToBundle(arguments: Array<out ActionArgument>): Bundle {
            val bundle = Bundle()
            for (arg in arguments) {
                arg.putIn(bundle)
            }
            return bundle
        }
    }


}
