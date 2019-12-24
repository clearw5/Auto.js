package com.stardust.automator.filter

import com.stardust.automator.test.TestUiObject
import com.stardust.automator.UiObject
import com.stardust.automator.search.DFS

import org.junit.Test
import java.util.Random

import org.junit.Assert.*

/**
 * Created by Stardust on 2017/5/5.
 */
class DfsFilterTest {

    private class RandomFilter : Filter {

        private val mRandom = Random()

        override fun filter(node: UiObject): Boolean {
            return mRandom.nextBoolean()
        }
    }

}