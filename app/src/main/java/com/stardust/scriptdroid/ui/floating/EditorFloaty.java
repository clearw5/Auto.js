package com.stardust.scriptdroid.ui.floating;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.stardust.enhancedfloaty.FloatyService;
import com.stardust.enhancedfloaty.FloatyWindow;
import com.stardust.enhancedfloaty.ResizableFloaty;
import com.stardust.enhancedfloaty.ResizableFloatyWindow;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.external.floatingwindow.FloatyWindowManger;
import com.stardust.scriptdroid.script.ScriptFile;
import com.stardust.scriptdroid.ui.edit.EditActivity_;
import com.stardust.scriptdroid.ui.edit.EditorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_NAME;
import static com.stardust.scriptdroid.ui.edit.EditorView.EXTRA_PATH;

/**
 * Created by Stardust on 2017/9/29.
 */

public class EditorFloaty implements ResizableFloaty {

    private Intent mIntent;

    @BindView(R.id.resizer)
    View mResizeIcon;
    @BindView(R.id.move_cursor)
    View mMoveIcon;

    private ResizableFloatyWindow mWindow;

    public EditorFloaty(Intent intent) {
        mIntent = intent;
    }

    public static void floatingEdit(Context context, Intent intent) {
        FloatyWindowManger.addWindow(context, new ResizableFloatyWindow(new EditorFloaty(intent)));
    }

    public static void floatingEdit(Context context, String path) {
        floatingEdit(context, null, path);
    }

    public static void floatingEdit(Context context, String name, String path) {
        floatingEdit(context, new Intent(context, EditActivity_.class)
                .putExtra(EXTRA_PATH, path)
                .putExtra(EXTRA_NAME, name));
    }

    public static void floatingEdit(Context context, ScriptFile file) {
        floatingEdit(context, file.getSimplifiedName(), file.getPath());
    }

    @Override
    public View inflateView(FloatyService floatyService, ResizableFloatyWindow resizableFloatyWindow) {
        mWindow = resizableFloatyWindow;
        View v = View.inflate(new ContextThemeWrapper(floatyService, R.style.AppTheme), R.layout.floating_editor, null);
        setUpViews(v);
        return v;
    }

    private void setUpViews(View v) {
        EditorView editorView = (EditorView) v.findViewById(R.id.editor_view);
        editorView.handleIntent(mIntent);
        ButterKnife.bind(this, v);
    }

    @OnClick(R.id.move_or_resize)
    void showOrHideMoveAndResizeIcon() {
        if (mResizeIcon.getVisibility() == View.VISIBLE) {
            mResizeIcon.setVisibility(View.GONE);
            mMoveIcon.setVisibility(View.GONE);
        } else {
            mResizeIcon.setVisibility(View.VISIBLE);
            mMoveIcon.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.close)
    void close() {
        mWindow.close();
    }

    @Nullable
    @Override
    public View getResizerView(View view) {
        return view.findViewById(R.id.resizer);
    }

    @Nullable
    @Override
    public View getMoveCursorView(View view) {
        return view.findViewById(R.id.move_cursor);
    }


}
