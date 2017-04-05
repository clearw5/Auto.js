package com.stardust.scriptdroid.ui.main.operation;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.autojs.script.FileScriptSource;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.scripts.ScriptFile;
import com.stardust.scriptdroid.scripts.ScriptFileList;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.external.shortcut.Shortcut;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;
import com.stardust.scriptdroid.ui.edit.EditActivity;
import com.stardust.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Stardust on 2017/1/23.
 */

public abstract class ScriptFileOperation {


    public static class ShowMessageEvent {
        public int messageResId;

        public ShowMessageEvent(int message) {
            this.messageResId = message;
        }
    }

    public static void openByOtherApps(ScriptFile scriptFile) {
        Uri uri = Uri.parse("file://" + scriptFile.getPath());
        App.getApp().startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "text/plain").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void createShortcut(ScriptFile scriptFile) {
        new Shortcut(App.getApp()).name(scriptFile.getSimplifiedName())
                .targetClass(ShortcutActivity.class)
                .icon(R.drawable.ic_node_js_black)
                .extras(new Intent().putExtra(CommonUtils.EXTRA_KEY_PATH, scriptFile.getPath()))
                .send();
    }


    public static void edit(ScriptFile file) {
        EditActivity.editFile(App.getApp(), file.getSimplifiedName(), file.getPath());
    }


    public abstract void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position);

    private String mName;

    public ScriptFileOperation(String name, int iconResId) {
        mName = name;
        mIconResId = iconResId;
    }

    private int mIconResId;

    public String getName() {
        return mName;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public static class Run extends ScriptFileOperation {

        public Run() {
            super(App.getApp().getString(R.string.text_run), R.drawable.ic_play_green);
        }

        @Override
        public void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            EventBus.getDefault().post(new ShowMessageEvent(R.string.text_start_running));
            ScriptFile scriptFile = scriptFileList.get(position);
            AutoJs.getInstance().getScriptEngineService().execute(new FileScriptSource(scriptFile));
        }
    }

    public static class Edit extends ScriptFileOperation {

        private static Edit instance = new Edit();

        public Edit() {
            super(App.getApp().getString(R.string.text_edit), R.drawable.ic_edit_green_48dp);
        }

        public static Edit getInstance() {
            return instance;
        }

        public static void setInstance(Edit instance) {
            Edit.instance = instance;
        }

        @Override
        public void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            ScriptFile scriptFile = scriptFileList.get(position);
            EditActivity.editFile(App.getApp(), scriptFile.getSimplifiedName(), scriptFile.getPath());
            //脚本
            //任务&控制台
            //教程
            //
        }
    }

    public static class OpenByOtherApp extends ScriptFileOperation {

        public OpenByOtherApp() {
            super(App.getApp().getString(R.string.text_open_by_other_apps), R.drawable.ic_open_in_new_green_48dp);
        }

        @Override
        public void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            ScriptFile scriptFile = scriptFileList.get(position);
            Uri uri = Uri.parse("file://" + scriptFile.getPath());
            App.getApp().startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "text/plain").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public static class Rename extends ScriptFileOperation {

        public Rename() {
            super(App.getApp().getString(R.string.text_rename), R.drawable.ic_rename_green);
        }

        @Override
        public void operate(final RecyclerView recyclerView, final ScriptFileList scriptFileList, final int position) {
            String oldName = scriptFileList.get(position).getSimplifiedName();
            new ThemeColorMaterialDialogBuilder(recyclerView.getContext())
                    .title(R.string.text_rename)
                    .checkBoxPrompt(App.getApp().getString(R.string.text_rename_file_meanwhile), false, null)
                    .input(App.getApp().getString(R.string.text_please_input_new_name), oldName, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            scriptFileList.rename(position, input.toString(), dialog.isPromptCheckBoxChecked());
                            recyclerView.getAdapter().notifyItemChanged(position);
                        }
                    })
                    .show();
        }
    }

    public static class CreateShortcut extends ScriptFileOperation {

        public CreateShortcut() {
            super(App.getApp().getString(R.string.text_send_shortcut), R.drawable.ic_shortcut_green);
        }

        @Override
        public void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            ScriptFile scriptFile = scriptFileList.get(position);
            new Shortcut(App.getApp()).name(scriptFile.getSimplifiedName())
                    .targetClass(ShortcutActivity.class)
                    .icon(R.drawable.ic_robot_green)
                    .extras(new Intent().putExtra("path", scriptFile.getPath()))
                    .send();
            EventBus.getDefault().post(R.string.text_already_create);
        }
    }

    public static class Remove extends ScriptFileOperation {

        public Remove() {
            super(App.getApp().getString(R.string.text_delete), R.drawable.ic_delete_green_48dp);
        }

        @Override
        public void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            scriptFileList.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
        }
    }

    public static class Delete extends ScriptFileOperation {

        public Delete() {
            super(App.getApp().getString(R.string.text_delete_absolutly), R.drawable.ic_delete_forever_green_48dp);
        }

        @Override
        public void operate(RecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            boolean succeed = scriptFileList.deleteFromFileSystem(position);
            EventBus.getDefault().post(new ShowMessageEvent(succeed ? R.string.text_already_delete : R.string.text_delete_failed));
            if (succeed)
                recyclerView.getAdapter().notifyItemRemoved(position);
        }
    }


}
