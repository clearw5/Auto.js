package com.stardust.scriptdroid.ui.edit;

import android.view.MenuItem;

import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.script.Scripts;

/**
 * Created by Stardust on 2017/9/28.
 */

public class EditorMenu {

    private EditorView mEditorView;

    public EditorMenu(EditorView editor) {
        mEditorView = editor;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_console:
                showConsole();
                return true;
            case R.id.action_log:
                showLog();
                return true;
            case R.id.action_editor_theme:
                mEditorView.selectEditorTheme();
                return true;
            case R.id.action_beautify:
                beautifyCode();
                return true;
            case R.id.action_open_by_other_apps:
                openByOtherApps();
                return true;
            case R.id.action_force_stop:
                forceStop();
                return true;
        }
        return false;
    }


    private void showLog() {
        AutoJs.getInstance().getScriptEngineService().getGlobalConsole().show();
    }

    private void showConsole() {
        mEditorView.showConsole();
    }


    private void forceStop() {
        mEditorView.forceStop();
    }

    private void openByOtherApps() {
        mEditorView.openByOtherApps();
    }

    private void beautifyCode() {
        mEditorView.beautifyCode();
    }

}
