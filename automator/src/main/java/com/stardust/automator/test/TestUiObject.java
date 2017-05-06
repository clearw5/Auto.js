package com.stardust.automator.test;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import com.stardust.automator.UiObject;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Stardust on 2017/5/5.
 */

public class TestUiObject extends UiObject {

    public static int count = 0;
    public static int max = 0;
    private static Random random = new Random();

    private int mHashCode = random.nextInt();
    private boolean mRecycled = false;
    private int mChildCount;

    public TestUiObject(int i) {
        super(null);
        count++;
        max = Math.max(max, count);
        mChildCount = i;
    }


    public TestUiObject() {
        this(Math.max(0, random.nextInt(6) - 2));
    }

    @Override
    public UiObject child(int i) {
        return new TestUiObject();
    }

    @Override
    public UiObject parent() {
        return new TestUiObject();
    }

    @Override
    public int getChildCount() {
        return mChildCount;
    }

    @Override
    public boolean isScrollable() {
        return random.nextInt(4) == 0;
    }

    @Override
    public boolean isClickable() {
        return random.nextBoolean();
    }

    @Override
    public void getBoundsInScreen(Rect outBounds) {
        int left = random.nextInt(1080);
        int top = random.nextInt(1920);
        int right = random.nextInt(1080 - left) + left;
        int bottom = random.nextInt(1920 - top) + top;
        outBounds.set(left, top, right, bottom);
    }

    @Override
    public boolean performAction(int action, Bundle bundle) {
        return random.nextBoolean();
    }

    @Override
    public boolean performAction(int action) {
        return random.nextBoolean();
    }

    @Override
    public void recycle() {
        if (mRecycled) {
            throw new IllegalStateException();
        }
        mRecycled = true;
        count--;
    }

    @Override
    public String toString() {
        return "UiObject@" + Integer.toHexString(hashCode());
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }
}
