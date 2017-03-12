package com.stardust.scriptdroid.external.floating_window.content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.external.floating_window.view.FloatingScriptFileListView;

import io.mattcarroll.hover.Navigator;
import io.mattcarroll.hover.NavigatorContent;

/**
 * Created by Stardust on 2017/3/12.
 */

public class ScritpListNavigatorContent implements NavigatorContent {

    private FloatingScriptFileListView mFloatingScriptFileListView;

    public ScritpListNavigatorContent(Context context) {
        mFloatingScriptFileListView = new FloatingScriptFileListView(new ContextThemeWrapper(context, R.style.AppTheme));
        mFloatingScriptFileListView.setScriptFileList(SharedPrefScriptFileList.getInstance());
    }

    @NonNull
    @Override
    public View getView() {
        return mFloatingScriptFileListView;
    }

    @Override
    public void onShown(@NonNull Navigator navigator) {

    }

    @Override
    public void onHidden() {

    }
}
