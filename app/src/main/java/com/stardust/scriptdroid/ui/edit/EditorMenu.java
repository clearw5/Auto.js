package com.stardust.scriptdroid.ui.edit;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.ClipboardUtil;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by Stardust on 2017/9/28.
 */

public class EditorMenu {

    private EditorView mEditorView;
    private Context mContext;
    private CodeMirrorEditor mEditor;

    public EditorMenu(EditorView editorView) {
        mEditorView = editorView;
        mContext = editorView.getContext();
        mEditor = editorView.mEditor;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log:
                showLog();
                return true;
            case R.id.action_force_stop:
                forceStop();
                return true;
            default:
                if (onEditOptionsSelected(item)) {
                    return true;
                }
                if (onRefactorOptionsSelected(item)) {
                    return true;
                }
                if (onJumpOptionsSelected(item)) {
                    return true;
                }
                if (onMoreOptionsSelected(item)) {
                    return true;
                }
        }
        return false;
    }

    private boolean onJumpOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_jump_to_line:
                jumpToLine();
                return true;
            case R.id.action_jump_to_def:
                mEditor.jumpToDef();
                return true;
            case R.id.action_jump_to_start:
                mEditor.jumpToStart();
                return true;
            case R.id.action_jump_to_end:
                mEditor.jumpToEnd();
                return true;
            case R.id.action_jump_to_line_start:
                mEditor.jumpToLineStart();
                return true;
            case R.id.action_jump_to_line_end:
                mEditor.jumpToLineEnd();
                return true;
        }
        return false;
    }

    private boolean onRefactorOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_beautify:
                beautifyCode();
                return true;
            case R.id.action_rename:
                mEditor.rename();
                return true;
            case R.id.action_select_variable:
                mEditor.selectName();
                return true;
        }
        return false;
    }


    private boolean onMoreOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_console:
                showConsole();
                return true;
            case R.id.action_show_type:
                mEditor.showType();
                return true;
            case R.id.action_editor_theme:
                mEditorView.selectEditorTheme();
                return true;
            case R.id.action_open_by_other_apps:
                openByOtherApps();
                return true;
            case R.id.action_info:
                showInfo();
                return true;

        }
        return false;
    }


    private boolean onEditOptionsSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_or_replace:
                findOrReplace();
                return true;
            case R.id.action_copy_all:
                copyAll();
                return true;
            case R.id.action_paste:
                paste();
                return true;
            case R.id.action_copy_line:
                copyLine();
                return true;
            case R.id.action_delete_line:
                deleteLine();
                return true;
            case R.id.action_clear:
                mEditor.setText("");
                return true;
        }
        return false;
    }

    private void jumpToLine() {
        mEditor.getLineCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer lineCount) throws Exception {
                        showJumpDialog(lineCount);
                    }
                });
    }

    private void showJumpDialog(final int lineCount) {
        String hint = "1 ~ " + lineCount;
        new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_jump_to_line)
                .input(hint, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@android.support.annotation.NonNull MaterialDialog dialog, CharSequence input) {
                        int line = Integer.parseInt(input.toString());
                        mEditor.jumpTo(line - 1, 0);
                    }
                })
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .show();
    }

    private void showInfo() {
        Observable.zip(mEditor.getText(), mEditor.getLineCount(), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(@NonNull String text, @NonNull Integer lineCount) throws Exception {
                String size = PFile.getHumanReadableSize(text.length());
                return String.format(Locale.getDefault(), mContext.getString(R.string.format_editor_info),
                        text.length(), lineCount, size);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String info) throws Exception {
                        showInfo(info);
                    }
                });

    }

    private void showInfo(String info) {
        new ThemeColorMaterialDialogBuilder(mContext)
                .title(R.string.text_info)
                .content(info)
                .show();
    }

    private void copyLine() {
        mEditor.getLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        ClipboardUtil.setClip(mContext, s);
                        Snackbar.make(mEditorView, R.string.text_already_copy_to_clip, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteLine() {
        mEditor.deleteLine();
    }

    private void paste() {
        mEditor.insert(ClipboardUtil.getClip(mContext).toString());
    }

    private void findOrReplace() {
        mEditor.getSelection()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        new FindOrReplaceDialogBuilder(mContext, mEditorView)
                                .setQueryIfNotEmpty(s)
                                .show();
                    }
                });

    }

    private void copyAll() {
        mEditor.getText().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                ClipboardUtil.setClip(mContext, s);
                Snackbar.make(mEditorView, R.string.text_already_copy_to_clip, Snackbar.LENGTH_SHORT).show();
            }
        });
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
