package com.stardust.scriptdroid.ui.edit.editor920;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.jecelyin.editor.v2.Pref;
import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.ui.IActivity;
import com.jecelyin.editor.v2.ui.TabManager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Stardust on 2017/1/30.
 */
public class Editor920Activity extends IActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pref.getInstance(this).setReadOnly(false);
        setPref(Pref.KEY_PREF_KEEP_BACKUP_FILE, false);

    }

    private void setPref(String key, Object value) {
        try {
            Field field = Pref.class.getDeclaredField("map");
            field.setAccessible(true);
            ((Map) field.get(Pref.getInstance(this))).put(key, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startPickPathActivity(String s, String s1) {

    }

    @Override
    public void doNextCommand() {

    }

    @Override
    public void setMenuStatus(int i, int i1) {

    }

    @Override
    public void doCommand(Command command) {

    }

    @Override
    public void openFile(String s, String s1, int i) {

    }

    @Override
    public void setFindFolderCallback(FolderChooserDialog.FolderCallback folderCallback) {

    }

    @Override
    public TabManager getTabManager() {
        return null;
    }

    @Override
    public void insertText(CharSequence text) {
        Command c = new Command(Command.CommandEnum.INSERT_TEXT);
        c.object = text;
        doCommand(c);
    }

    @Override
    public String getCurrentLang() {
        return null;
    }

    @Override
    public void onDocumentChanged(int i) {

    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {

    }

    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception ignored) {

        }
    }
}
