package com.stardust.automator

import android.graphics.Rect
import android.os.Build

import com.stardust.automator.filter.BooleanFilter
import com.stardust.automator.filter.BoundsFilter
import com.stardust.automator.filter.ClassNameFilter
import com.stardust.automator.filter.DescFilter
import com.stardust.automator.filter.DfsFilter
import com.stardust.automator.filter.IdFilter
import com.stardust.automator.filter.IntFilter
import com.stardust.automator.filter.ListFilter
import com.stardust.automator.filter.PackageNameFilter
import com.stardust.automator.filter.TextFilter
import com.stardust.automator.simple_action.Able
import com.stardust.util.Supplier

import java.util.ArrayList
import java.util.LinkedList
import java.util.Queue

/**
 * Created by Stardust on 2017/3/8.
 */

open class UiGlobalSelector {

    private val mFilters = LinkedList<ListFilter>()

    //// 第一类筛选条件

    open fun id(id: String): UiGlobalSelector {
        mFilters.add(IdFilter.equals(id))
        return this
    }

    fun idContains(str: String): UiGlobalSelector {
        mFilters.add(IdFilter.contains(str))
        return this
    }

    open fun idStartsWith(prefix: String): UiGlobalSelector {
        mFilters.add(IdFilter.startsWith(prefix))
        return this
    }

    fun idEndsWith(suffix: String): UiGlobalSelector {
        mFilters.add(IdFilter.endsWith(suffix))
        return this
    }

    open fun idMatches(regex: String): UiGlobalSelector {
        mFilters.add(IdFilter.matches(regex))
        return this
    }

    fun text(text: String): UiGlobalSelector {
        mFilters.add(TextFilter.equals(text))
        return this
    }

    fun textContains(str: String): UiGlobalSelector {
        mFilters.add(TextFilter.contains(str))
        return this
    }

    fun textStartsWith(prefix: String): UiGlobalSelector {
        mFilters.add(TextFilter.startsWith(prefix))
        return this
    }

    fun textEndsWith(suffix: String): UiGlobalSelector {
        mFilters.add(TextFilter.endsWith(suffix))
        return this
    }

    open fun textMatches(regex: String): UiGlobalSelector {
        mFilters.add(TextFilter.matches(regex))
        return this
    }

    fun desc(desc: String): UiGlobalSelector {
        mFilters.add(DescFilter.equals(desc))
        return this
    }

    fun descContains(str: String): UiGlobalSelector {
        mFilters.add(DescFilter.contains(str))
        return this
    }

    fun descStartsWith(prefix: String): UiGlobalSelector {
        mFilters.add(DescFilter.startsWith(prefix))
        return this
    }

    fun descEndsWith(suffix: String): UiGlobalSelector {
        mFilters.add(DescFilter.endsWith(suffix))
        return this
    }

    open fun descMatches(regex: String): UiGlobalSelector {
        mFilters.add(DescFilter.matches(regex))
        return this
    }

    fun className(className: String): UiGlobalSelector {
        mFilters.add(ClassNameFilter.equals(className))
        return this
    }

    fun classNameContains(str: String): UiGlobalSelector {
        mFilters.add(ClassNameFilter.contains(str))
        return this
    }

    fun classNameStartsWith(prefix: String): UiGlobalSelector {
        mFilters.add(ClassNameFilter.startsWith(prefix))
        return this
    }

    fun classNameEndsWith(suffix: String): UiGlobalSelector {
        mFilters.add(ClassNameFilter.endsWith(suffix))
        return this
    }

    open fun classNameMatches(regex: String): UiGlobalSelector {
        mFilters.add(ClassNameFilter.matches(regex))
        return this
    }

    fun packageName(packageName: String): UiGlobalSelector {
        mFilters.add(PackageNameFilter.equals(packageName))
        return this
    }

    fun packageNameContains(str: String): UiGlobalSelector {
        mFilters.add(PackageNameFilter.contains(str))
        return this
    }

    fun packageNameStartsWith(prefix: String): UiGlobalSelector {
        mFilters.add(PackageNameFilter.startsWith(prefix))
        return this
    }

    fun packageNameEndsWith(suffix: String): UiGlobalSelector {
        mFilters.add(PackageNameFilter.endsWith(suffix))
        return this
    }

    open fun packageNameMatches(regex: String): UiGlobalSelector {
        mFilters.add(PackageNameFilter.matches(regex))
        return this
    }

    fun bounds(l: Int, t: Int, r: Int, b: Int): UiGlobalSelector {
        mFilters.add(BoundsFilter(Rect(l, t, r, b), BoundsFilter.TYPE_EQUALS))
        return this
    }

    fun boundsInside(l: Int, t: Int, r: Int, b: Int): UiGlobalSelector {
        mFilters.add(BoundsFilter(Rect(l, t, r, b), BoundsFilter.TYPE_INSIDE))
        return this
    }

    fun boundsContains(l: Int, t: Int, r: Int, b: Int): UiGlobalSelector {
        mFilters.add(BoundsFilter(Rect(l, t, r, b), BoundsFilter.TYPE_CONTAINS))
        return this
    }

    fun drawingOrder(order: Int): UiGlobalSelector {
        mFilters.add(object : DfsFilter() {
            override fun isIncluded(nodeInfo: UiObject): Boolean {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && nodeInfo.drawingOrder == order
            }

            override fun toString(): String {
                return "drawingOrder($order)"
            }
        })
        return this
    }

    //// 第二类筛选条件 -able

    fun checkable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CHECKABLE, b])
        return this
    }

    fun checked(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CHECKED, b])
        return this
    }

    fun focusable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.FOCUSABLE, b])
        return this
    }

    fun focused(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.FOCUSED, b])
        return this
    }

    fun visibleToUser(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.VISIBLE_TO_USER, b])
        return this
    }

    fun accessibilityFocused(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.ACCESSIBILITY_FOCUSED, b])
        return this
    }

    fun selected(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.SELECTED, b])
        return this
    }

    fun clickable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CLICKABLE, b])
        return this
    }

    fun longClickable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.LONG_CLICKABLE, b])
        return this
    }

    fun enabled(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.ENABLED, b])
        return this
    }

    fun password(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.PASSWORD, b])
        return this
    }

    fun scrollable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.SCROLLABLE, b])
        return this
    }

    fun editable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.EDITABLE, b])
        return this
    }

    fun contentInvalid(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CONTENT_INVALID, b])
        return this
    }

    fun contextClickable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CONTEXT_CLICKABLE, b])
        return this
    }

    fun multiLine(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.MULTI_LINE, b])
        return this
    }

    fun dismissable(b: Boolean): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.DISMISSABLE, b])
        return this
    }

    fun checkable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CHECKABLE, true])
        return this
    }

    fun checked(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CHECKED, true])
        return this
    }

    fun focusable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.FOCUSABLE, true])
        return this
    }

    fun focused(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.FOCUSED, true])
        return this
    }

    fun visibleToUser(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.VISIBLE_TO_USER, true])
        return this
    }

    fun accessibilityFocused(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.ACCESSIBILITY_FOCUSED, true])
        return this
    }

    fun selected(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.SELECTED, true])
        return this
    }

    fun clickable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CLICKABLE, true])
        return this
    }

    fun longClickable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.LONG_CLICKABLE, true])
        return this
    }

    fun enabled(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.ENABLED, true])
        return this
    }

    fun password(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.PASSWORD, true])
        return this
    }

    fun scrollable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.SCROLLABLE, true])
        return this
    }

    fun editable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.EDITABLE, true])
        return this
    }

    fun contentInvalid(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CONTENT_INVALID, true])
        return this
    }

    fun contextClickable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.CONTEXT_CLICKABLE, true])
        return this
    }

    fun multiLine(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.MULTI_LINE, true])
        return this
    }

    fun dismissable(): UiGlobalSelector {
        mFilters.add(BooleanFilter[BooleanFilter.DISMISSABLE, true])
        return this
    }


    //第三类 int
    fun depth(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.DEPTH, d))
        return this
    }

    fun row(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.ROW, d))
        return this
    }

    fun rowCount(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.ROW_COUNT, d))
        return this
    }

    fun rowSpan(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.ROW_SPAN, d))
        return this
    }

    fun column(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.COLUMN, d))
        return this
    }

    fun columnCount(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.COLUMN_COUNT, d))
        return this
    }

    fun columnSpan(d: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.COLUMN_SPAN, d))
        return this
    }

    fun indexInParent(index: Int): UiGlobalSelector {
        mFilters.add(IntFilter(IntFilter.INDEX_IN_PARENT, index))
        return this
    }


    fun filter(filter: BooleanFilter.BooleanSupplier): UiGlobalSelector {
        mFilters.add(object : DfsFilter() {
            override fun isIncluded(nodeInfo: UiObject): Boolean {
                return filter[nodeInfo]
            }
        })
        return this
    }

    fun findOf(node: UiObject, max: Int): UiObjectCollection {
        return UiObjectCollection.of(findAndReturnList(node, max))
    }

    fun findOf(node: UiObject): UiObjectCollection {
        return findOf(node, Int.MAX_VALUE)
    }

    fun findAndReturnList(node: UiObject, max: Int = Int.MAX_VALUE): List<UiObject> {
        var list: List<UiObject> = listOf(node)
        for (filter in mFilters) {
            val oldMax = filter.maxCount
            filter.maxCount = max
            list = filter.filter(list)
            filter.maxCount = oldMax
        }
        return list
    }

    fun findOneOf(node: UiObject): UiObject? {
        val collection = findOf(node, 1)
        return if (collection.size() == 0) {
            null
        } else collection[0]
    }

    fun addFilter(filter: ListFilter): UiGlobalSelector {
        mFilters.add(filter)
        return this
    }

    override fun toString(): String {
        val str = StringBuilder()
        for (filter in mFilters) {
            str.append(filter.toString()).append(".")
        }
        if (str.isNotEmpty()) {
            str.deleteCharAt(str.length - 1)
        }
        return str.toString()
    }
}
