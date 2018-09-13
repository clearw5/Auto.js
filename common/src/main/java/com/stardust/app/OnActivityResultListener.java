package com.stardust.app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/3/5.
 */

public interface OnActivityResultListener {

    void onActivityResult(int requestCode, int resultCode, Intent data);

    interface ObservableActivity {
        @NonNull
        ActivityResultObserver getActivityResultObserver();
    }

    class ActivityResultObserver implements OnActivityResultListener {

        private SparseArray<OnActivityResultListener> mSpecialDelegate = new SparseArray<>();
        private List<OnActivityResultListener> mDelegates = new ArrayList<>();

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            OnActivityResultListener delegate = mSpecialDelegate.get(requestCode);
            if (delegate != null) {
                delegate.onActivityResult(requestCode, resultCode, data);
            }
            for (OnActivityResultListener d : mDelegates) {
                d.onActivityResult(requestCode, resultCode, data);
            }
        }

        public void addListener(OnActivityResultListener delegate) {
            mDelegates.add(delegate);
        }

        public void addListener(int requestCode, OnActivityResultListener delegate) {
            mSpecialDelegate.put(requestCode, delegate);
        }

        public void removeListener(OnActivityResultListener delegate) {
            if (mDelegates.remove(delegate)) {
                mSpecialDelegate.removeAt(mSpecialDelegate.indexOfValue(delegate));
            }
        }
    }

}
