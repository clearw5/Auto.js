package com.stardust.automator

import android.os.Bundle

import com.stardust.util.Consumer

import java.util.ArrayList
import java.util.Collections

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_COLUMN_INT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_PROGRESS_VALUE
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_ROW_INT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_END_INT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_START_INT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLEAR_FOCUS
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLICK
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_COLLAPSE
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_COPY
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CUT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_DISMISS
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_EXPAND
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_FOCUS
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_LONG_CLICK
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_PASTE
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SELECT
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SET_SELECTION
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SET_TEXT
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

class UiObjectCollection private constructor(private val mNodes: List<UiObject?>) {


    val isEmpty: Boolean
        get() = mNodes.isEmpty()

    fun toArray(): Array<UiObject?> {
        return mNodes.toTypedArray()
    }

    fun performAction(action: Int): Boolean {
        var fail = false
        for (node in mNodes) {
            val succeed = node?.performAction(action) ?: false
            if (!succeed) {
                fail = true
            }
        }
        return !fail
    }

    fun performAction(action: Int, vararg arguments: ActionArgument): Boolean {
        var fail = false
        val bundle = argumentsToBundle(arguments)
        for (node in mNodes) {
            val succeed = node?.performAction(action, bundle) ?: false
            if (succeed) {
                fail = true
            }
        }
        return !fail
    }

    private fun argumentsToBundle(arguments: Array<out ActionArgument>): Bundle {
        val bundle = Bundle()
        for (arg in arguments) {
            arg.putIn(bundle)
        }
        return bundle
    }

    fun click(): Boolean {
        return performAction(ACTION_CLICK)
    }

    fun longClick(): Boolean {
        return performAction(ACTION_LONG_CLICK)
    }

    fun accessibilityFocus(): Boolean {
        return performAction(ACTION_ACCESSIBILITY_FOCUS)
    }

    fun clearAccessibilityFocus(): Boolean {
        return performAction(ACTION_CLEAR_ACCESSIBILITY_FOCUS)
    }

    fun focus(): Boolean {
        return performAction(ACTION_FOCUS)
    }

    fun clearFocus(): Boolean {
        return performAction(ACTION_CLEAR_FOCUS)
    }

    fun copy(): Boolean {
        return performAction(ACTION_COPY)
    }

    fun paste(): Boolean {
        return performAction(ACTION_PASTE)
    }

    fun select(): Boolean {
        return performAction(ACTION_SELECT)
    }

    fun cut(): Boolean {
        return performAction(ACTION_CUT)
    }

    fun collapse(): Boolean {
        return performAction(ACTION_COLLAPSE)
    }

    fun expand(): Boolean {
        return performAction(ACTION_EXPAND)
    }

    fun dismiss(): Boolean {
        return performAction(ACTION_DISMISS)
    }

    fun show(): Boolean {
        return performAction(ACTION_SHOW_ON_SCREEN.id)
    }

    fun scrollForward(): Boolean {
        return performAction(ACTION_SCROLL_FORWARD)
    }

    fun scrollBackward(): Boolean {
        return performAction(ACTION_SCROLL_BACKWARD)
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
        return performAction(ACTION_SET_SELECTION,
                ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_START_INT, s),
                ActionArgument.IntActionArgument(ACTION_ARGUMENT_SELECTION_END_INT, e))
    }

    fun setText(text: CharSequence): Boolean {
        return performAction(ACTION_SET_TEXT,
                ActionArgument.CharSequenceActionArgument(ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text))
    }

    fun setProgress(value: Float): Boolean {
        return performAction(ACTION_SET_PROGRESS.id,
                ActionArgument.FloatActionArgument(ACTION_ARGUMENT_PROGRESS_VALUE, value))

    }

    fun scrollTo(row: Int, column: Int): Boolean {
        return performAction(ACTION_SCROLL_TO_POSITION.id,
                ActionArgument.IntActionArgument(ACTION_ARGUMENT_ROW_INT, row),
                ActionArgument.IntActionArgument(ACTION_ARGUMENT_COLUMN_INT, column))
    }

    operator fun get(i: Int): UiObject? {
        return mNodes[i]
    }

    fun indexOf(o: UiObject?): Int {
        return mNodes.indexOf(o)
    }


    fun lastIndexOf(o: UiObject?): Int {
        return mNodes.lastIndexOf(o)
    }

    fun size(): Int {
        return mNodes.size
    }


    operator fun contains(o: UiObject?): Boolean {
        return mNodes.contains(o)
    }

    operator fun iterator(): Iterator<UiObject?> {
        return mNodes.iterator()
    }

    fun each(consumer: Consumer<UiObject>): UiObjectCollection {
        for (uiObject in mNodes) {
            consumer.accept(uiObject)
        }
        return this
    }

    fun find(selector: UiGlobalSelector): UiObjectCollection {
        val list = ArrayList<UiObject?>()
        for (`object` in mNodes) {
            `object`?.let {
                list.addAll(selector.findOf(it).mNodes)
            }
        }
        return of(list)
    }

    fun findOne(selector: UiGlobalSelector): UiObject? {
        for (`object` in mNodes) {
            val result = `object`?.run { selector.findOneOf(this) }
            if (result != null)
                return result
        }
        return null
    }

    fun empty(): Boolean {
        return size() == 0
    }

    fun nonEmpty(): Boolean {
        return size() != 0
    }

    companion object {

        val EMPTY = UiObjectCollection.of(emptyList())

        fun of(list: List<UiObject?>): UiObjectCollection {
            return UiObjectCollection(list)
        }
    }

}
