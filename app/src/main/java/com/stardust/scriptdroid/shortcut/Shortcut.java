package com.stardust.scriptdroid.shortcut;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Stardust on 2017/1/20.
 */

public class Shortcut {

    private Context mContext;
    private String mName;
    private String mTargetClass;
    private String mTargetPackage;
    private Intent.ShortcutIconResource mIcon;
    private boolean mDuplicate = false;
    private Intent mLaunchIntent = new Intent();

    public Shortcut(Context context) {
        mContext = context;
        mTargetPackage = mContext.getPackageName();
    }

    public Shortcut(String name, Context context) {
        this(context);
        mName = name;
    }

    public Shortcut name(String name) {
        mName = name;
        return this;
    }

    public Shortcut targetPackage(String targetPackage) {
        mTargetPackage = targetPackage;
        return this;
    }

    public Shortcut targetClass(String targetClass) {
        mTargetClass = targetClass;
        return this;
    }

    public Shortcut targetClass(Class<?> targetClass) {
        mTargetClass = targetClass.getName();
        return this;
    }


    public Shortcut icon(Intent.ShortcutIconResource icon) {
        mIcon = icon;
        return this;
    }

    public Shortcut icon(int resId) {
        mIcon = Intent.ShortcutIconResource.fromContext(mContext, resId);
        return this;
    }

    public Shortcut duplicate(boolean duplicate) {
        mDuplicate = duplicate;
        return this;
    }

    public String getName() {
        return mName;
    }

    public Intent.ShortcutIconResource getIcon() {
        return mIcon;
    }

    public boolean isDuplicate() {
        return mDuplicate;
    }

    private String getClassName() {
        return mTargetClass;
    }

    private String getPackageName() {
        return mTargetPackage;
    }

    public Intent getCreateIntent() {
        return new Intent(Intent.ACTION_CREATE_SHORTCUT)
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, getName())
                .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, getIcon())
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, getLaunchIntent())
                .putExtra("duplicate", isDuplicate())
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    }

    public Intent getLaunchIntent() {
        mLaunchIntent.setClassName(getPackageName(), getClassName());
        return mLaunchIntent;
    }

    public void send() {
        mContext.sendBroadcast(getCreateIntent());
    }

    public Shortcut extras(Bundle bundle) {
        mLaunchIntent.putExtras(bundle);
        return this;
    }

    public Shortcut extras(Intent intent) {
        mLaunchIntent.putExtras(intent);
        return this;
    }
}
