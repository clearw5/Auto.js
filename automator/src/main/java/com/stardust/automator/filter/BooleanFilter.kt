package com.stardust.automator.filter

import android.os.Build
import androidx.annotation.RequiresApi

import com.stardust.automator.UiObject

import java.util.HashMap

/**
 * Created by Stardust on 2017/3/9.
 */

class BooleanFilter(private val mBooleanSupplier: BooleanSupplier, private val mExceptedValue: Boolean) : Filter {

    interface BooleanSupplier {

        operator fun get(node: UiObject): Boolean

    }

    override fun filter(node: UiObject): Boolean {
        return mBooleanSupplier[node] == mExceptedValue
    }

    override fun toString(): String {
        return mBooleanSupplier.toString() + "(" + mExceptedValue + ")"
    }

    companion object {

        private val cache = HashMap<BooleanSupplier, Array<BooleanFilter>>()

        operator fun get(supplier: BooleanSupplier, b: Boolean): BooleanFilter {
            var booleanFilters: Array<BooleanFilter>? = cache[supplier]
            if (booleanFilters == null) {
                booleanFilters = Array(2) {
                    BooleanFilter(supplier, it != 0)
                }
                cache[supplier] = booleanFilters
            }
            val i = if (b) 1 else 0
            return booleanFilters[i]
        }

        val CHECKABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isCheckable
            }

            override fun toString(): String {
                return "checkable"
            }
        }

        val CHECKED: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isChecked
            }

            override fun toString(): String {
                return "checked"
            }
        }

        val FOCUSABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isFocusable
            }

            override fun toString(): String {
                return "focusable"
            }
        }

        val FOCUSED: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isFocused
            }

            override fun toString(): String {
                return "focused"
            }
        }

        val VISIBLE_TO_USER: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isVisibleToUser
            }

            override fun toString(): String {
                return "visibleToUser"
            }
        }

        val ACCESSIBILITY_FOCUSED: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isAccessibilityFocused
            }

            override fun toString(): String {
                return "accessibilityFocused"
            }
        }

        val SELECTED: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isSelected
            }

            override fun toString(): String {
                return "selected"
            }
        }

        val CLICKABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isClickable
            }

            override fun toString(): String {
                return "clickable"
            }
        }

        val LONG_CLICKABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isLongClickable
            }

            override fun toString(): String {
                return "longClickable"
            }
        }

        val ENABLED: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isEnabled
            }

            override fun toString(): String {
                return "enabled"
            }
        }


        val PASSWORD: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isPassword
            }

            override fun toString(): String {
                return "password"
            }
        }

        val SCROLLABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isScrollable
            }

            override fun toString(): String {
                return "scrollable"
            }
        }

        val EDITABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isEditable
            }

            override fun toString(): String {
                return "editable"
            }
        }
        val CONTENT_INVALID: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isContentInvalid
            }

            override fun toString(): String {
                return "contentInvalid"
            }
        }

        val CONTEXT_CLICKABLE: BooleanSupplier = object : BooleanSupplier {

            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun get(node: UiObject): Boolean {
                return node.isContextClickable
            }

            override fun toString(): String {
                return "checkable"
            }
        }

        val MULTI_LINE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isMultiLine
            }

            override fun toString(): String {
                return "multiLine"
            }
        }

        val DISMISSABLE: BooleanSupplier = object : BooleanSupplier {

            override fun get(node: UiObject): Boolean {
                return node.isDismissable
            }

            override fun toString(): String {
                return "dismissable"
            }
        }
    }
}
