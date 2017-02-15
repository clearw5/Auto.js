package com.stardust.scriptdroid.bounds_assist;

/**
 * Created by Stardust on 2017/2/4.
 */

public interface BoundsAssistClipList {

    interface OnClipChangedListener {
        void onClipRemove(int position);

        void onClipInsert(int position);

        void onChange();
    }

    int size();

    String get(int i);

    void add(String clip);

    void setOnClipChangedListener(OnClipChangedListener listener);

}
