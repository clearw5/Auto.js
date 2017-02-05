package com.stardust.scriptdroid.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.EditActivity;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ShortcutActivity;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.shortcut.Shortcut;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/23.
 */

public abstract class ScriptFileOperation {

    private static List<String> operationNames = new ArrayList<>();
    private static List<ScriptFileOperation> operations = new ArrayList<>();

    public static ScriptFileOperation getOperation(int index) {
        return operations.get(index);
    }

    public static List<String> getOperationNames() {
        return operationNames;
    }

    public abstract void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position);

    private static void addOperation(String name, int iconResId, ScriptFileOperation operation) {
        operation.mName = name;
        operation.mIconResId = iconResId;
        operationNames.add(name);
        operations.add(operation);
    }

    private String mName;
    private int mIconResId;

    public String getName() {
        return mName;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public static class Run extends ScriptFileOperation {

        static {
            addOperation(App.getApp().getString(R.string.text_run), R.drawable.ic_play_green, new Run());
        }

        @Override
        public void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            Snackbar.make(recyclerView, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
            ScriptFile scriptFile = scriptFileList.get(position);
            scriptFile.run();
        }
    }

    public static class Edit extends ScriptFileOperation {

        static {
            //addOperation(App.getApp().getString(R.string.text_edit)", R.drawable.ic_edit_green_48dp, new Edit());
        }

        @Override
        public void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            Context context = recyclerView.getContext();
            ScriptFile scriptFile = scriptFileList.get(position);
            EditActivity.editFile(context, scriptFile.name, scriptFile.path);
        }
    }

    public static class OpenByOtherApp extends ScriptFileOperation {

        static {
            addOperation(App.getApp().getString(R.string.text_open_by_other_apps), R.drawable.ic_open_in_new_green_48dp, new OpenByOtherApp());
        }

        @Override
        public void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            Context context = recyclerView.getContext();
            ScriptFile scriptFile = scriptFileList.get(position);
            Uri uri = Uri.parse("file://" + scriptFile.path);
            context.startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "text/plain"));
        }
    }

    public static class Rename extends ScriptFileOperation {

        static {
            addOperation(App.getApp().getString(R.string.text_rename), R.drawable.ic_rename_green, new Rename());
        }

        @Override
        public void operate(final ScriptListRecyclerView recyclerView, final ScriptFileList scriptFileList, final int position) {
            String oldName = scriptFileList.get(position).name;
            new MaterialDialog.Builder(recyclerView.getContext())
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

        static {
            addOperation(App.getApp().getString(R.string.text_send_shortcut), R.drawable.ic_shortcut_green, new CreateShortcut());
        }


        @Override
        public void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            Context context = recyclerView.getContext();
            ScriptFile scriptFile = scriptFileList.get(position);
            new Shortcut(context).name(scriptFile.name)
                    .targetClass(ShortcutActivity.class)
                    .icon(R.drawable.ic_robot_green)
                    .extras(new Intent().putExtra("path", scriptFile.path))
                    .send();
            Snackbar.make(recyclerView, R.string.text_already_create, Snackbar.LENGTH_SHORT).show();
        }
    }

    public static class Remove extends ScriptFileOperation {

        static {
            addOperation(App.getApp().getString(R.string.text_delete), R.drawable.ic_delete_green_48dp, new Remove());
        }

        @Override
        public void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            scriptFileList.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
        }
    }

    public static class Delete extends ScriptFileOperation {

        static {
            addOperation(App.getApp().getString(R.string.text_delete_absolutly), R.drawable.ic_delete_forever_green_48dp, new Delete());
        }

        @Override
        public void operate(ScriptListRecyclerView recyclerView, ScriptFileList scriptFileList, int position) {
            boolean succeed = scriptFileList.deleteFromFileSystem(position);
            Snackbar.make(recyclerView, succeed ? R.string.text_already_delete : R.string.text_delete_failed, Snackbar.LENGTH_SHORT).show();
            recyclerView.getAdapter().notifyItemRemoved(position);
        }
    }


}
