package com.stardust.automator.filter

import com.stardust.automator.UiObject

import java.util.ArrayList

/**
 * Created by Stardust on 2017/3/9.
 */

interface ListFilter {

    fun filter(nodes: List<UiObject>): List<UiObject>

    abstract class Default : Filter, ListFilter {

        override fun filter(nodes: List<UiObject>): List<UiObject> {
            val list = ArrayList<UiObject>()
            for (node in nodes) {
                list.addAll(filter(node))
            }
            return list
        }
    }

}
