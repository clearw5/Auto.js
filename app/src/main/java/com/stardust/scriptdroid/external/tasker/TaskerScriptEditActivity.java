package com.stardust.scriptdroid.external.tasker;

import android.app.Activity;
import android.content.Intent;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.edit.EditorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_CONTENT;
import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_NAME;
import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_RUN_ENABLED;
import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_SAVE_ENABLED;

/**
 * Created by Stardust on 2017/4/5.
 */
@EActivity(R.layout.activity_tasker_script_edit)
public class TaskerScriptEditActivity extends BaseActivity {

    public static final int REQUEST_CODE = "Love you. Can we go back?".hashCode() >> 16;

    public static void edit(Activity activity, String title, String summary, String content) {
        activity.startActivityForResult(new Intent(activity, TaskerScriptEditActivity_.class)
                .putExtra(EXTRA_CONTENT, content)
                .putExtra("summary", summary)
                .putExtra(EXTRA_NAME, title), REQUEST_CODE);
    }

    @ViewById(R.id.editor_view)
    EditorView mEditorView;

    @AfterViews
    void setUpViews() {
        mEditorView.handleIntent(getIntent()
                .putExtra(EXTRA_RUN_ENABLED, false)
                .putExtra(EXTRA_SAVE_ENABLED, false));
        BaseActivity.setToolbarAsBack(this, R.id.toolbar, mEditorView.getName());
    }


    @Override
    public void finish() {
        mEditorView.getEditor().getText().subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                setResult(RESULT_OK, new Intent().putExtra(EXTRA_CONTENT, s));
                TaskerScriptEditActivity.super.finish();
            }
        });
    }

}
