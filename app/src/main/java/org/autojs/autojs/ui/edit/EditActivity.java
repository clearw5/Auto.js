package org.autojs.autojs.ui.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.core.permission.OnRequestPermissionsResultCallback;
import com.stardust.autojs.core.permission.PermissionRequestProxyActivity;
import com.stardust.autojs.core.permission.RequestPermissionCallbacks;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.pio.PFiles;

import org.autojs.autojs.R;
import org.autojs.autojs.storage.file.TmpScriptFiles;
import org.autojs.autojs.tool.Observers;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.ui.main.MainActivity_;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.autojs.autojs.ui.edit.EditorView.EXTRA_CONTENT;
import static org.autojs.autojs.ui.edit.EditorView.EXTRA_NAME;
import static org.autojs.autojs.ui.edit.EditorView.EXTRA_PATH;
import static org.autojs.autojs.ui.edit.EditorView.EXTRA_READ_ONLY;

/**
 * Created by Stardust on 2017/1/29.
 */
@EActivity(R.layout.activity_edit)
public class EditActivity extends BaseActivity implements OnActivityResultDelegate.DelegateHost, PermissionRequestProxyActivity {

    private OnActivityResultDelegate.Mediator mMediator = new OnActivityResultDelegate.Mediator();
    private static final String LOG_TAG = "EditActivity";

    @ViewById(R.id.editor_view)
    EditorView mEditorView;

    private EditorMenu mEditorMenu;
    private RequestPermissionCallbacks mRequestPermissionCallbacks = new RequestPermissionCallbacks();
    private boolean mNewTask;

    public static void editFile(Context context, String path, boolean newTask) {
        editFile(context, null, path, newTask);
    }

    public static void editFile(Context context, Uri uri, boolean newTask) {
        context.startActivity(newIntent(context, newTask)
                .setData(uri));
    }

    public static void editFile(Context context, String name, String path, boolean newTask) {
        context.startActivity(newIntent(context, newTask)
                .putExtra(EXTRA_PATH, path)
                .putExtra(EXTRA_NAME, name));
    }

    public static void viewContent(Context context, String name, String content, boolean newTask) {
        context.startActivity(newIntent(context, newTask)
                .putExtra(EXTRA_CONTENT, content)
                .putExtra(EXTRA_NAME, name)
                .putExtra(EXTRA_READ_ONLY, true));
    }

    private static Intent newIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, EditActivity_.class);
        if (newTask || !(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewTask = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
    }

    @SuppressLint("CheckResult")
    @AfterViews
    void setUpViews() {
        mEditorView.handleIntent(getIntent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.emptyConsumer(),
                        ex -> onLoadFileError(ex.getMessage()));
        mEditorMenu = new EditorMenu(mEditorView);
        setUpToolbar();
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return super.onWindowStartingActionMode(callback);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return super.onWindowStartingActionMode(callback, type);
    }

    private void onLoadFileError(String message) {
        new ThemeColorMaterialDialogBuilder(this)
                .title(getString(R.string.text_cannot_read_file))
                .content(message)
                .positiveText(R.string.text_exit)
                .cancelable(false)
                .onPositive((dialog, which) -> finish())
                .show();
    }

    private void setUpToolbar() {
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mEditorView.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mEditorMenu.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onPrepareOptionsMenu: " + menu);
        boolean isScriptRunning = mEditorView.getScriptExecutionId() != ScriptExecution.NO_ID;
        MenuItem forceStopItem = menu.findItem(R.id.action_force_stop);
        forceStopItem.setEnabled(isScriptRunning);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        Log.d(LOG_TAG, "onActionModeStarted: " + mode);
        Menu menu = mode.getMenu();
        MenuItem item = menu.getItem(menu.size() - 1);
        menu.add(item.getGroupId(), R.id.action_delete_line, 10000, R.string.text_delete_line);
        menu.add(item.getGroupId(), R.id.action_copy_line, 20000, R.string.text_copy_line);
        super.onActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeStarted(@NonNull androidx.appcompat.view.ActionMode mode) {
        Log.d(LOG_TAG, "onSupportActionModeStarted: mode = " + mode);
        super.onSupportActionModeStarted(mode);
    }

    @Nullable
    @Override
    public androidx.appcompat.view.ActionMode onWindowStartingSupportActionMode(@NonNull androidx.appcompat.view.ActionMode.Callback callback) {
        Log.d(LOG_TAG, "onWindowStartingSupportActionMode: callback = " + callback);
        return super.onWindowStartingSupportActionMode(callback);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        Log.d(LOG_TAG, "startActionMode: callback = " + callback + ", type = " + type);
        return super.startActionMode(callback, type);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        Log.d(LOG_TAG, "startActionMode: callback = " + callback);
        return super.startActionMode(callback);
    }

    @Override
    public void onBackPressed() {
        if (!mEditorView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        if (mEditorView.isTextChanged()) {
            showExitConfirmDialog();
            return;
        }
        finishAndRemoveFromRecents();
    }

    private void finishAndRemoveFromRecents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            super.finish();
        }
        if (mNewTask) {
            startActivity(new Intent(this, MainActivity_.class));
        }
    }

    private void showExitConfirmDialog() {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_alert)
                .content(R.string.edit_exit_without_save_warn)
                .positiveText(R.string.text_cancel)
                .negativeText(R.string.text_save_and_exit)
                .neutralText(R.string.text_exit_directly)
                .onNegative((dialog, which) -> {
                    mEditorView.saveFile();
                    finishAndRemoveFromRecents();
                })
                .onNeutral((dialog, which) -> finishAndRemoveFromRecents())
                .show();
    }

    @Override
    protected void onDestroy() {
        mEditorView.destroy();
        super.onDestroy();
    }

    @NonNull
    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mMediator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mMediator.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mEditorView.isTextChanged()) {
            return;
        }
        String text = mEditorView.getEditor().getText();
        if (text.length() < 256 * 1024) {
            outState.putString("text", text);
        } else {
            File tmp = saveToTmpFile(text);
            if (tmp != null) {
                outState.putString("path", tmp.getPath());
            }

        }
    }

    private File saveToTmpFile(String text) {
        try {
            File tmp = TmpScriptFiles.create(this);
            Observable.just(text)
                    .observeOn(Schedulers.io())
                    .subscribe(t -> PFiles.write(tmp, t));
            return tmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String text = savedInstanceState.getString("text");
        if (text != null) {
            mEditorView.setRestoredText(text);
            return;
        }
        String path = savedInstanceState.getString("path");
        if (path != null) {
            Observable.just(path)
                    .observeOn(Schedulers.io())
                    .map(PFiles::read)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(t -> mEditorView.getEditor().setText(t), Throwable::printStackTrace);
        }
    }

    @Override
    public void addRequestPermissionsCallback(OnRequestPermissionsResultCallback callback) {
        mRequestPermissionCallbacks.addCallback(callback);
    }

    @Override
    public boolean removeRequestPermissionsCallback(OnRequestPermissionsResultCallback callback) {
        return mRequestPermissionCallbacks.removeCallback(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
