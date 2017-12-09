package com.stardust.scriptdroid.ui.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/11/2.
 */

public class EditorActionModeCallback implements ActionMode.Callback {


    private static final int ID_SELECT_ALL = 34275489;

    private ActionMode.Callback mCallback;
    private CodeMirrorEditor mEditor;

    public EditorActionModeCallback(ActionMode.Callback callback, CodeMirrorEditor editor) {
        mCallback = callback;
        mEditor = editor;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return mCallback.onCreateActionMode(mode, menu);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return mCallback.onPrepareActionMode(mode, menu);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_line:
                mEditor.deleteLine();
                return true;
            case R.id.action_copy_line:
                mEditor.copyLine();
                mode.finish();
                return true;
        }
        return mCallback.onActionItemClicked(mode, item);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mCallback.onDestroyActionMode(mode);

    }
}
