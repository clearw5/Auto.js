package com.stardust.scriptdroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.common.SaveListener;
import com.jecelyin.editor.v2.ui.EditorDelegate;
import com.jecelyin.editor.v2.view.EditorView;
import com.jecelyin.editor.v2.view.menu.MenuDef;
import com.stardust.scriptdroid.droid.Droid;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;
import com.stardust.scriptdroid.editor920.Editor920Activity;
import com.stardust.scriptdroid.ui.AssistClipListRecyclerView;
import com.stardust.scriptdroid.ui.EditSideMenuFragment;
import com.stardust.scriptdroid.ui.FunctionListRecyclerView;
import com.stardust.scriptdroid.widget.ToolbarMenuItem;
import com.stardust.util.SparseArrayEntries;
import com.stardust.view.ViewBinder;
import com.stardust.view.ViewBinding;

import java.io.File;

/**
 * Created by Stardust on 2017/1/29.
 */

public class EditActivity extends Editor920Activity {

    private static final String KEY_EDIT_ACTIVITY_FIRST_USE = "KEY_EDIT_ACTIVITY_FIRST_USE";

    public static void editFile(Context context, String path) {
        editFile(context, null, path);
    }

    public static void editFile(Context context, String name, String path) {
        context.startActivity(new Intent(context, EditActivity.class)
                .putExtra("path", path)
                .putExtra("name", name));
    }

    private String mName;
    private File mFile;
    private View mView;
    private DrawerLayout mDrawerLayout;
    private EditorDelegate mEditorDelegate;
    private SparseArray<ToolbarMenuItem> mMenuMap;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        DroidRuntime.setContext(this);
        handleIntent();
        setUpUI();
        setUpEditor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        openDrawerIfFirstUse();
    }

    private void openDrawerIfFirstUse() {
        if (Pref.def().getBoolean(KEY_EDIT_ACTIVITY_FIRST_USE, true)) {
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }, 1000);
            Pref.def().edit().putBoolean(KEY_EDIT_ACTIVITY_FIRST_USE, false).apply();
        }
    }

    private void handleIntent() {
        String path = getIntent().getStringExtra("path");
        mName = getIntent().getStringExtra("name");
        if (path == null) {
            finish();
        } else {
            mFile = new File(path);
            if (mName == null) {
                mName = mFile.getName();
            }
        }
    }

    private void setUpUI() {
        setTheme(R.style.EditorTheme);
        mView = View.inflate(this, R.layout.activity_edit, null);
        mDrawerLayout = (DrawerLayout) mView.findViewById(R.id.drawer_layout);
        setContentView(mView);
        initSideMenuFragment();
        setUpToolbar();
        initMenuItem();
        ViewBinder.bind(this);
    }

    private void initSideMenuFragment() {
        EditSideMenuFragment.setFragment(EditActivity.this, R.id.fragment_edit_side_menu)
                .setOnFunctionClickListener(new FunctionListRecyclerView.OnFunctionClickListener() {
                    @Override
                    public void onClick(FunctionListRecyclerView.Function function, int position) {
                        insertText(function.name);
                        mDrawerLayout.closeDrawer(GravityCompat.END);
                    }
                })
                .setOnClipClickListener(new AssistClipListRecyclerView.OnClipClickListener() {
                    @Override
                    public void onClick(String clip, int position) {
                        insertText(clip);
                        mDrawerLayout.closeDrawer(GravityCompat.END);
                    }
                });

    }

    private void setUpEditor() {
        if (mFile != null) {
            mEditorDelegate = new EditorDelegate(0, mFile, 0, null);
            EditorView editorView = (EditorView) findViewById(R.id.editor);
            mEditorDelegate.setEditorView(editorView);
        }
    }


    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @ViewBinding.Click(R.id.run)
    private void runAndSaveFileIFNeeded() {
        if (mEditorDelegate.isChanged()) {
            saveFile(false, new SaveListener() {
                @Override
                public void onSaved() {
                    run();
                }
            });
        } else {
            run();
        }
    }

    private void saveFile(boolean toast, SaveListener listener) {
        Command command = new Command(Command.CommandEnum.SAVE);
        command.args = new Bundle();
        command.args.putBoolean("is_cluster", !toast);
        command.object = listener;
        mEditorDelegate.doCommand(command);
    }

    private void run() {
        Snackbar.make(mView, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
        setMenuStatus(R.id.run, MenuDef.STATUS_DISABLED);
        Droid.getInstance().runScriptFile(mFile, new Droid.OnRunFinishedListener() {
            @Override
            public void onRunFinished(Object result, final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMenuStatus(R.id.run, MenuDef.STATUS_NORMAL);
                        if (e != null)
                            Snackbar.make(mView, getString(R.string.text_error) + ": " + e.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
                    }
                });
            }
        });
    }

    @ViewBinding.Click(R.id.undo)
    private void undo() {
        Command command = new Command(Command.CommandEnum.UNDO);
        mEditorDelegate.doCommand(command);
    }

    @ViewBinding.Click(R.id.redo)
    private void redo() {
        Command command = new Command(Command.CommandEnum.REDO);
        mEditorDelegate.doCommand(command);
    }


    @ViewBinding.Click(R.id.save)
    private void saveFile() {
        saveFile(false, null);
    }

    private void initMenuItem() {
        mMenuMap = new SparseArrayEntries<ToolbarMenuItem>()
                .entry(com.jecelyin.editor.v2.R.id.m_redo, (ToolbarMenuItem) findViewById(R.id.redo))
                .entry(com.jecelyin.editor.v2.R.id.m_undo, (ToolbarMenuItem) findViewById(R.id.undo))
                .entry(com.jecelyin.editor.v2.R.id.m_save, (ToolbarMenuItem) findViewById(R.id.save))
                .entry(R.id.run, (ToolbarMenuItem) findViewById(R.id.run))
                .sparseArray();
    }

    public void setMenuStatus(int menuResId, int status) {
        ToolbarMenuItem menuItem = mMenuMap.get(menuResId);
        if (menuItem == null)
            return;
        boolean disabled = status == MenuDef.STATUS_DISABLED;
        menuItem.setEnabled(!disabled);
    }

    @Override
    public void finish() {
        if (mEditorDelegate.isChanged()) {
            showExitConfirmDialog();
        } else {
            super.finish();
        }
    }

    private void showExitConfirmDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.text_alert)
                .content(R.string.edit_exit_without_save_warn)
                .positiveText(R.string.text_cancel)
                .negativeText(R.string.text_save_and_exit)
                .neutralText(R.string.text_exit_directly)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        saveFile(true, null);
                        EditActivity.super.finish();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditActivity.super.finish();
                    }
                })
                .show();
    }

    @Override
    public void doCommand(Command command) {
        mEditorDelegate.doCommand(command);
    }
}
