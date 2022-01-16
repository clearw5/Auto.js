package com.stardust.enhancedfloaty;

import android.view.View;

import java.util.Stack;

/**
 * Created by Stardust on 2017/3/11.
 */

public class ViewStack {

    public interface CurrentViewSetter {
        void setCurrentView(View v);
    }

    public interface NavigableView {
        void goBack();
    }

    private Stack<View> mStack = new Stack<>();
    private CurrentViewSetter mCurrentViewSetter;

    public ViewStack(CurrentViewSetter currentViewSetter) {
        mCurrentViewSetter = currentViewSetter;
    }

    public void navigateTo(View v) {
        mStack.push(v);
        mCurrentViewSetter.setCurrentView(v);
    }

    public boolean canGoBack() {
        return mStack.size() > 1;
    }

    public void goBack() {
        mCurrentViewSetter.setCurrentView(mStack.pop());
    }

    public void goBackToFirst() {
        while (mStack.size() > 1) {
            mStack.pop();
        }
        mCurrentViewSetter.setCurrentView(mStack.peek());
    }

    public void setRootView(View view) {
        mStack.clear();
        mStack.push(view);
    }


}
