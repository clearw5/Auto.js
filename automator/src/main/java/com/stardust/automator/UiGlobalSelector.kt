package com.stardust.automator

import android.graphics.Rect
import android.os.Build
import com.stardust.automator.filter.*
import com.stardust.automator.search.BFS
import com.stardust.automator.search.DFS
import com.stardust.automator.search.SearchAlgorithm
import java.lang.IllegalArgumentException

/**
 * Created by Stardust on 2017/3/8.
 */

open class UiGlobalSelector {

    private val mSelector = Selector()
    private var mSearchAlgorithm: SearchAlgorithm = DFS

    //// 第一类筛选条件

    open fun id(id: String): UiGlobalSelector {
        mSelector.add(IdFilter.equals(id))
        return this
    }

    fun idContains(str: String): UiGlobalSelector {
        mSelector.add(IdFilter.contains(str))
        return this
    }

    open fun idStartsWith(prefix: String): UiGlobalSelector {
        mSelector.add(IdFilter.startsWith(prefix))
        return this
    }

    fun idEndsWith(suffix: String): UiGlobalSelector {
        mSelector.add(IdFilter.endsWith(suffix))
        return this
    }

    open fun idMatches(regex: String): UiGlobalSelector {
        mSelector.add(IdFilter.matches(regex))
        return this
    }

    fun text(text: String): UiGlobalSelector {
        mSelector.add(TextFilters.equals(text))
        return this
    }

    fun textContains(str: String): UiGlobalSelector {
        mSelector.add(TextFilters.contains(str))
        return this
    }

    fun textStartsWith(prefix: String): UiGlobalSelector {
        mSelector.add(TextFilters.startsWith(prefix))
        return this
    }

    fun textEndsWith(suffix: String): UiGlobalSelector {
        mSelector.add(TextFilters.endsWith(suffix))
        return this
    }

    open fun textMatches(regex: String): UiGlobalSelector {
        mSelector.add(TextFilters.matches(regex))
        return this
    }

    fun desc(desc: String): UiGlobalSelector {
        mSelector.add(DescFilters.equals(desc))
        return this
    }

    fun descContains(str: String): UiGlobalSelector {
        mSelector.add(DescFilters.contains(str))
        return this
    }

    fun descStartsWith(prefix: String): UiGlobalSelector {
        mSelector.add(DescFilters.startsWith(prefix))
        return this
    }

    fun descEndsWith(suffix: String): UiGlobalSelector {
        mSelector.add(DescFilters.endsWith(suffix))
        return this
    }

    open fun descMatches(regex: String): UiGlobalSelector {
        mSelector.add(DescFilters.matches(regex))
        return this
    }

    fun className(className: String): UiGlobalSelector {
        mSelector.add(ClassNameFilters.equals(className))
        return this
    }

    fun classNameContains(str: String): UiGlobalSelector {
        mSelector.add(ClassNameFilters.contains(str))
        return this
    }

    fun classNameStartsWith(prefix: String): UiGlobalSelector {
        mSelector.add(ClassNameFilters.startsWith(prefix))
        return this
    }

    fun classNameEndsWith(suffix: String): UiGlobalSelector {
        mSelector.add(ClassNameFilters.endsWith(suffix))
        return this
    }

    open fun classNameMatches(regex: String): UiGlobalSelector {
        mSelector.add(ClassNameFilters.matches(regex))
        return this
    }

    fun packageName(packageName: String): UiGlobalSelector {
        mSelector.add(PackageNameFilter.equals(packageName))
        return this
    }

    fun packageNameContains(str: String): UiGlobalSelector {
        mSelector.add(PackageNameFilter.contains(str))
        return this
    }

    fun packageNameStartsWith(prefix: String): UiGlobalSelector {
        mSelector.add(PackageNameFilter.startsWith(prefix))
        return this
    }

    fun packageNameEndsWith(suffix: String): UiGlobalSelector {
        mSelector.add(PackageNameFilter.endsWith(suffix))
        return this
    }

    open fun packageNameMatches(regex: String): UiGlobalSelector {
        mSelector.add(PackageNameFilter.matches(regex))
        return this
    }

    fun bounds(l: Int, t: Int, r: Int, b: Int): UiGlobalSelector {
        mSelector.add(BoundsFilter(Rect(l, t, r, b), BoundsFilter.TYPE_EQUALS))
        return this
    }

    fun boundsInside(l: Int, t: Int, r: Int, b: Int): UiGlobalSelector {
        mSelector.add(BoundsFilter(Rect(l, t, r, b), BoundsFilter.TYPE_INSIDE))
        return this
    }

    fun boundsContains(l: Int, t: Int, r: Int, b: Int): UiGlobalSelector {
        mSelector.add(BoundsFilter(Rect(l, t, r, b), BoundsFilter.TYPE_CONTAINS))
        return this
    }

    fun drawingOrder(order: Int): UiGlobalSelector {
        mSelector.add(object : Filter {
            override fun filter(node: UiObject): Boolean {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && node.drawingOrder == order
            }

            override fun toString(): String {
                return "drawingOrder($order)"
            }
        })
        return this
    }

    //// 第二类筛选条件 -able

    fun checkable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CHECKABLE, b])
        return this
    }

    fun checked(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CHECKED, b])
        return this
    }

    fun focusable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.FOCUSABLE, b])
        return this
    }

    fun focused(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.FOCUSED, b])
        return this
    }

    fun visibleToUser(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.VISIBLE_TO_USER, b])
        return this
    }

    fun accessibilityFocused(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.ACCESSIBILITY_FOCUSED, b])
        return this
    }

    fun selected(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.SELECTED, b])
        return this
    }

    fun clickable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CLICKABLE, b])
        return this
    }

    fun longClickable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.LONG_CLICKABLE, b])
        return this
    }

    fun enabled(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.ENABLED, b])
        return this
    }

    fun password(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.PASSWORD, b])
        return this
    }

    fun scrollable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.SCROLLABLE, b])
        return this
    }

    fun editable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.EDITABLE, b])
        return this
    }

    fun contentInvalid(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CONTENT_INVALID, b])
        return this
    }

    fun contextClickable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CONTEXT_CLICKABLE, b])
        return this
    }

    fun multiLine(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.MULTI_LINE, b])
        return this
    }

    fun dismissable(b: Boolean): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.DISMISSABLE, b])
        return this
    }

    fun checkable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CHECKABLE, true])
        return this
    }

    fun checked(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CHECKED, true])
        return this
    }

    fun focusable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.FOCUSABLE, true])
        return this
    }

    fun focused(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.FOCUSED, true])
        return this
    }

    fun visibleToUser(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.VISIBLE_TO_USER, true])
        return this
    }

    fun accessibilityFocused(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.ACCESSIBILITY_FOCUSED, true])
        return this
    }

    fun selected(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.SELECTED, true])
        return this
    }

    fun clickable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CLICKABLE, true])
        return this
    }

    fun longClickable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.LONG_CLICKABLE, true])
        return this
    }

    fun enabled(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.ENABLED, true])
        return this
    }

    fun password(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.PASSWORD, true])
        return this
    }

    fun scrollable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.SCROLLABLE, true])
        return this
    }

    fun editable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.EDITABLE, true])
        return this
    }

    fun contentInvalid(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CONTENT_INVALID, true])
        return this
    }

    fun contextClickable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.CONTEXT_CLICKABLE, true])
        return this
    }

    fun multiLine(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.MULTI_LINE, true])
        return this
    }

    fun dismissable(): UiGlobalSelector {
        mSelector.add(BooleanFilter[BooleanFilter.DISMISSABLE, true])
        return this
    }


    //第三类 int
    fun depth(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.DEPTH, d))
        return this
    }

    fun row(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.ROW, d))
        return this
    }

    fun rowCount(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.ROW_COUNT, d))
        return this
    }

    fun rowSpan(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.ROW_SPAN, d))
        return this
    }

    fun column(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.COLUMN, d))
        return this
    }

    fun columnCount(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.COLUMN_COUNT, d))
        return this
    }

    fun columnSpan(d: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.COLUMN_SPAN, d))
        return this
    }

    fun indexInParent(index: Int): UiGlobalSelector {
        mSelector.add(IntFilter(IntFilter.INDEX_IN_PARENT, index))
        return this
    }


    fun filter(filter: BooleanFilter.BooleanSupplier): UiGlobalSelector {
        mSelector.add(object : Filter {
            override fun filter(node: UiObject): Boolean {
                return filter[node]
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


    fun findOneOf(node: UiObject): UiObject? {
        val collection = findOf(node, 1)
        return if (collection.size() == 0) {
            null
        } else collection[0]
    }

    fun addFilter(filter: Filter): UiGlobalSelector {
        mSelector.add(filter)
        return this
    }

    fun algorithm(algorithm: String): UiGlobalSelector {
        if (algorithm.equals("BFS", true)) {
            mSearchAlgorithm = BFS
            return this
        }
        if (algorithm.equals("DFS", true)) {
            mSearchAlgorithm = DFS
            return this
        }
        throw IllegalArgumentException("unknown algorithm: $algorithm")
    }

    fun findAndReturnList(node: UiObject, max: Int = Int.MAX_VALUE): List<UiObject> {
        return mSearchAlgorithm.search(node, mSelector, max)
    }


    override fun toString(): String {
        return mSelector.toString()
    }
}
