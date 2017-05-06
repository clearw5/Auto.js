package com.stardust.automator.filter;

import com.stardust.automator.test.TestUiObject;
import com.stardust.automator.UiObject;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Stardust on 2017/5/5.
 */
public class DfsFilterTest {

    private static class RandomDfsFilter extends DfsFilter {

        private Random mRandom = new Random();

        @Override
        protected boolean isIncluded(UiObject nodeInfo) {
            return mRandom.nextBoolean();
        }
    }

    @Test
    public void filter() throws Exception {
        DfsFilter filter = new RandomDfsFilter();
        UiObject root = new TestUiObject(10);
        List<UiObject> list = filter.filter(root);
        for (UiObject uiObject : list) {
            if (root != uiObject)
                uiObject.recycle();
        }
        System.out.println(TestUiObject.max);
        assertEquals(1, TestUiObject.count);
    }

}