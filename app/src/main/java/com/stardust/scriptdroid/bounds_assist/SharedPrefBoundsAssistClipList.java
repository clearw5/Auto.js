package com.stardust.scriptdroid.bounds_assist;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.scriptdroid.App;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stardust on 2017/2/4.
 */

public class SharedPrefBoundsAssistClipList implements BoundsAssistClipList {

    private static final Gson GSON = new Gson();


    private static SharedPrefBoundsAssistClipList instance = new SharedPrefBoundsAssistClipList(App.getApp());
    private OnClipChangedListener mOnClipChangedListener;

    public static SharedPrefBoundsAssistClipList getInstance() {
        return instance;
    }

    private static final int MAX_SIZE = 10;
    private static final String SHARED_PREF_NAME = "SharedPrefBoundsAssistClipList";
    private SharedPreferences mSharedPreferences;
    private List<String> mAssistClipList = new LinkedList<>();

    public SharedPrefBoundsAssistClipList(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        readFromSharedPref();
    }

    private void readFromSharedPref() {
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> l = GSON.fromJson(mSharedPreferences.getString(SHARED_PREF_NAME, ""), type);
        if (l != null)
            mAssistClipList.addAll(l);
    }

    @Override
    public synchronized int size() {
        return mAssistClipList.size();
    }

    @Override
    public synchronized String get(int i) {
        return mAssistClipList.get(i);
    }

    @Override
    public synchronized void add(String clip) {
        mAssistClipList.add(0, clip);
        notifyInsert();
        if (mAssistClipList.size() > MAX_SIZE) {
            mAssistClipList.remove(mAssistClipList.size() - 1);
            notifyRemove();
        }
        syncWithSharedPref();
    }

    private void notifyRemove() {
        if (mOnClipChangedListener != null)
            mOnClipChangedListener.onClipRemove(mAssistClipList.size());
    }

    private void notifyInsert() {
        if (mOnClipChangedListener != null) {
            mOnClipChangedListener.onClipInsert(0);
        }
    }

    @Override
    public void setOnClipChangedListener(OnClipChangedListener listener) {
        mOnClipChangedListener = listener;
    }

    private void syncWithSharedPref() {
        mSharedPreferences.edit().putString(SHARED_PREF_NAME, GSON.toJson(mAssistClipList)).apply();
    }

}
