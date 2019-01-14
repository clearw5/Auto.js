package com.stardust.autojs.core.ui.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.stardust.app.DialogUtils;
import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.util.UiHandler;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2018/4/17.
 */

public class JsDialog {

    private final EventEmitter mEmitter;
    private final UiHandler mUiHandler;
    private final MaterialDialog mDialog;
    private final JsDialogBuilder mBuilder;

    public JsDialog(JsDialogBuilder builder, EventEmitter emitter, UiHandler uiHandler) {
        mBuilder = builder;
        mDialog = builder.build();
        mEmitter = emitter;
        mUiHandler = uiHandler;
    }

    public JsDialog show() {
        checkWindowType();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.show();
        } else {
            mUiHandler.post(mDialog::show);
        }
        mBuilder.onShowCalled();
        return this;
    }

    private void checkWindowType() {
        Context context = mDialog.getContext();
        if (!DialogUtils.isActivityContext(context)) {
            Window window = mDialog.getWindow();
            if (window != null){
                int type;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                window.setType(type);
            }
        }
    }

    private DialogAction getDialogAction(String action) {
        switch (action) {
            case "positive":
                return DialogAction.POSITIVE;
            case "negative":
                return DialogAction.NEGATIVE;
            case "neutral":
                return DialogAction.NEUTRAL;
            default:
                throw new IllegalArgumentException("unknown action " + action);
        }
    }

    public int getProgress() {
        return getCurrentProgress();
    }

    public String getActionButton(String action) {
        return getActionButton(getDialogAction(action)).getText().toString();
    }

    public void setActionButton(String action, String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setActionButton(getDialogAction(action), text);
        } else {
            mUiHandler.post(() -> setActionButton(getDialogAction(action), text));
        }
    }


    public MaterialDialog.Builder getBuilder() {
        return mDialog.getBuilder();
    }

    public void setTypeface(TextView target, Typeface t) {
        mDialog.setTypeface(target, t);
    }

    @Nullable
    public Object getTag() {
        return mDialog.getTag();
    }

    public boolean onItemSelected(MaterialDialog dialog, View view, int position, CharSequence text, boolean longPress) {
        return mDialog.onItemSelected(dialog, view, position, text, longPress);
    }

    public RecyclerView getRecyclerView() {
        return mDialog.getRecyclerView();
    }

    public boolean isPromptCheckBoxChecked() {
        return mDialog.isPromptCheckBoxChecked();
    }

    public void setPromptCheckBoxChecked(boolean checked) {
        mDialog.setPromptCheckBoxChecked(checked);
    }

    public void onClick(View v) {
        mDialog.onClick(v);
    }

    public MDButton getActionButton(@NonNull DialogAction which) {
        return mDialog.getActionButton(which);
    }

    public View getView() {
        return mDialog.getView();
    }

    @Nullable
    public EditText getInputEditText() {
        return mDialog.getInputEditText();
    }

    public TextView getTitleView() {
        return mDialog.getTitleView();
    }

    public ImageView getIconView() {
        return mDialog.getIconView();
    }

    @Nullable
    public TextView getContentView() {
        return mDialog.getContentView();
    }

    @Nullable
    public View getCustomView() {
        return mDialog.getCustomView();
    }

    @UiThread
    public void setActionButton(@NonNull DialogAction which, CharSequence title) {
        mDialog.setActionButton(which, title);
    }

    public void setActionButton(DialogAction which, int titleRes) {
        mDialog.setActionButton(which, titleRes);
    }

    public boolean hasActionButtons() {
        return mDialog.hasActionButtons();
    }

    public int numberOfActionButtons() {
        return mDialog.numberOfActionButtons();
    }

    @UiThread
    public void setTitle(@NonNull CharSequence newTitle) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setTitle(newTitle);
        } else {
            mUiHandler.post(() -> mDialog.setTitle(newTitle));
        }
    }

    @UiThread
    public void setTitle(int newTitleRes) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setTitle(newTitleRes);
        } else {
            mUiHandler.post(() -> mDialog.setTitle(newTitleRes));
        }
    }

    @UiThread
    public void setTitle(int newTitleRes, @Nullable Object... formatArgs) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setTitle(newTitleRes, formatArgs);
        } else {
            mUiHandler.post(() -> mDialog.setTitle(newTitleRes, formatArgs));
        }
    }

    @UiThread
    public void setIcon(int resId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setIcon(resId);
        } else {
            mUiHandler.post(() -> mDialog.setIcon(resId));
        }
    }

    @UiThread
    public void setIcon(Drawable d) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setIcon(d);
        } else {
            mUiHandler.post(() -> mDialog.setIcon(d));
        }
    }

    @UiThread
    public void setIconAttribute(int attrId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setIconAttribute(attrId);
        } else {
            mUiHandler.post(() -> mDialog.setIconAttribute(attrId));
        }
    }

    @UiThread
    public void setContent(CharSequence newContent) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setContent(newContent);
        } else {
            mUiHandler.post(() -> mDialog.setContent(newContent));
        }
    }

    @UiThread
    public void setContent(int newContentRes) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setContent(newContentRes);
        } else {
            mUiHandler.post(() -> mDialog.setContent(newContentRes));
        }
    }

    @UiThread
    public void setContent(int newContentRes, @Nullable Object... formatArgs) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setContent(newContentRes, formatArgs);
        } else {
            mUiHandler.post(() -> mDialog.setContent(newContentRes, formatArgs));
        }
    }

    @Deprecated
    public void setMessage(CharSequence message) {
        mDialog.setMessage(message);
    }

    @Nullable
    public ArrayList<CharSequence> getItems() {
        return mDialog.getItems();
    }

    @UiThread
    public void setItems(CharSequence... items) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setItems(items);
        } else {
            mUiHandler.post(() -> mDialog.setItems(items));
        }
    }

    @UiThread
    public void notifyItemInserted(int index) {
        mDialog.notifyItemInserted(index);
    }

    @UiThread
    public void notifyItemChanged(int index) {
        mDialog.notifyItemChanged(index);
    }

    @UiThread
    public void notifyItemsChanged() {
        mDialog.notifyItemsChanged();
    }

    public int getCurrentProgress() {
        return mDialog.getCurrentProgress();
    }

    public ProgressBar getProgressBar() {
        return mDialog.getProgressBar();
    }

    public void incrementProgress(int by) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.incrementProgress(by);
        } else {
            mUiHandler.post(() -> mDialog.incrementProgress(by));
        }
    }

    public void setProgress(int progress) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mDialog.setProgress(progress);
        } else {
            mUiHandler.post(() -> mDialog.setProgress(progress));
        }
    }

    public void setMaxProgress(int max) {
        mDialog.setMaxProgress(max);
    }

    public boolean isIndeterminateProgress() {
        return mDialog.isIndeterminateProgress();
    }

    public int getMaxProgress() {
        return mDialog.getMaxProgress();
    }

    public void setProgressPercentFormat(NumberFormat format) {
        mDialog.setProgressPercentFormat(format);
    }

    public void setProgressNumberFormat(String format) {
        mDialog.setProgressNumberFormat(format);
    }

    public boolean isCancelled() {
        return mDialog.isCancelled();
    }

    public int getSelectedIndex() {
        return mDialog.getSelectedIndex();
    }

    @Nullable
    public Integer[] getSelectedIndices() {
        return mDialog.getSelectedIndices();
    }

    @UiThread
    public void setSelectedIndex(int index) {
        mDialog.setSelectedIndex(index);
    }

    @UiThread
    public void setSelectedIndices(@NonNull Integer[] indices) {
        mDialog.setSelectedIndices(indices);
    }

    public void clearSelectedIndices() {
        mDialog.clearSelectedIndices();
    }

    public void clearSelectedIndices(boolean sendCallback) {
        mDialog.clearSelectedIndices(sendCallback);
    }

    public void selectAllIndices() {
        mDialog.selectAllIndices();
    }

    public void selectAllIndices(boolean sendCallback) {
        mDialog.selectAllIndices(sendCallback);
    }

    public void onShow(DialogInterface dialog) {
        mDialog.onShow(dialog);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public View findViewById(int id) {
        return mDialog.findViewById(id);
    }

    public void setOnShowListener(DialogInterface.OnShowListener listener) {
        mDialog.setOnShowListener(listener);
    }

    @Deprecated
    public void setContentView(int layoutResID) throws IllegalAccessError {
        mDialog.setContentView(layoutResID);
    }

    @Deprecated
    public void setContentView(@NonNull View view) throws IllegalAccessError {
        mDialog.setContentView(view);
    }

    @Deprecated
    public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) throws IllegalAccessError {
        mDialog.setContentView(view, params);
    }

    public Context getContext() {
        return mDialog.getContext();
    }

    public ActionBar getActionBar() {
        return mDialog.getActionBar();
    }

    public void setOwnerActivity(@NonNull Activity activity) {
        mDialog.setOwnerActivity(activity);
    }

    public Activity getOwnerActivity() {
        return mDialog.getOwnerActivity();
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void create() {
        mDialog.create();
    }

    public void hide() {
        mDialog.hide();
    }

    public Bundle onSaveInstanceState() {
        return mDialog.onSaveInstanceState();
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mDialog.onRestoreInstanceState(savedInstanceState);
    }

    public Window getWindow() {
        return mDialog.getWindow();
    }

    public View getCurrentFocus() {
        return mDialog.getCurrentFocus();
    }

    public void addContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        mDialog.addContentView(view, params);
    }

    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        return mDialog.onKeyDown(keyCode, event);
    }

    public boolean onKeyLongPress(int keyCode, @NonNull KeyEvent event) {
        return mDialog.onKeyLongPress(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return mDialog.onKeyUp(keyCode, event);
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, @NonNull KeyEvent event) {
        return mDialog.onKeyMultiple(keyCode, repeatCount, event);
    }

    public void onBackPressed() {
        mDialog.onBackPressed();
    }

    public boolean onKeyShortcut(int keyCode, @NonNull KeyEvent event) {
        return mDialog.onKeyShortcut(keyCode, event);
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return mDialog.onTouchEvent(event);
    }

    public boolean onTrackballEvent(@NonNull MotionEvent event) {
        return mDialog.onTrackballEvent(event);
    }

    public boolean onGenericMotionEvent(@NonNull MotionEvent event) {
        return mDialog.onGenericMotionEvent(event);
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        mDialog.onWindowAttributesChanged(params);
    }

    public void onContentChanged() {
        mDialog.onContentChanged();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        mDialog.onWindowFocusChanged(hasFocus);
    }

    public void onAttachedToWindow() {
        mDialog.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        mDialog.onDetachedFromWindow();
    }

    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mDialog.dispatchKeyEvent(event);
    }

    public boolean dispatchKeyShortcutEvent(@NonNull KeyEvent event) {
        return mDialog.dispatchKeyShortcutEvent(event);
    }

    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        return mDialog.dispatchTouchEvent(ev);
    }

    public boolean dispatchTrackballEvent(@NonNull MotionEvent ev) {
        return mDialog.dispatchTrackballEvent(ev);
    }

    public boolean dispatchGenericMotionEvent(@NonNull MotionEvent ev) {
        return mDialog.dispatchGenericMotionEvent(ev);
    }

    public boolean dispatchPopulateAccessibilityEvent(@NonNull AccessibilityEvent event) {
        return mDialog.dispatchPopulateAccessibilityEvent(event);
    }

    public View onCreatePanelView(int featureId) {
        return mDialog.onCreatePanelView(featureId);
    }

    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        return mDialog.onCreatePanelMenu(featureId, menu);
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return mDialog.onPreparePanel(featureId, view, menu);
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        return mDialog.onMenuOpened(featureId, menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return mDialog.onMenuItemSelected(featureId, item);
    }

    public void onPanelClosed(int featureId, Menu menu) {
        mDialog.onPanelClosed(featureId, menu);
    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return mDialog.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        return mDialog.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return mDialog.onOptionsItemSelected(item);
    }

    public void onOptionsMenuClosed(@NonNull Menu menu) {
        mDialog.onOptionsMenuClosed(menu);
    }

    public void openOptionsMenu() {
        mDialog.openOptionsMenu();
    }

    public void closeOptionsMenu() {
        mDialog.closeOptionsMenu();
    }

    public void invalidateOptionsMenu() {
        mDialog.invalidateOptionsMenu();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mDialog.onCreateContextMenu(menu, v, menuInfo);
    }

    public void registerForContextMenu(@NonNull View view) {
        mDialog.registerForContextMenu(view);
    }

    public void unregisterForContextMenu(@NonNull View view) {
        mDialog.unregisterForContextMenu(view);
    }

    public void openContextMenu(@NonNull View view) {
        mDialog.openContextMenu(view);
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return mDialog.onContextItemSelected(item);
    }

    public void onContextMenuClosed(@NonNull Menu menu) {
        mDialog.onContextMenuClosed(menu);
    }

    public boolean onSearchRequested(@NonNull SearchEvent searchEvent) {
        return mDialog.onSearchRequested(searchEvent);
    }

    public boolean onSearchRequested() {
        return mDialog.onSearchRequested();
    }

    public SearchEvent getSearchEvent() {
        return mDialog.getSearchEvent();
    }

    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return mDialog.onWindowStartingActionMode(callback);
    }

    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return mDialog.onWindowStartingActionMode(callback, type);
    }

    public void onActionModeStarted(ActionMode mode) {
        mDialog.onActionModeStarted(mode);
    }

    public void onActionModeFinished(ActionMode mode) {
        mDialog.onActionModeFinished(mode);
    }

    public void takeKeyEvents(boolean get) {
        mDialog.takeKeyEvents(get);
    }

    public boolean requestWindowFeature(int featureId) {
        return mDialog.requestWindowFeature(featureId);
    }

    public void setFeatureDrawableResource(int featureId, int resId) {
        mDialog.setFeatureDrawableResource(featureId, resId);
    }

    public void setFeatureDrawableUri(int featureId, @Nullable Uri uri) {
        mDialog.setFeatureDrawableUri(featureId, uri);
    }

    public void setFeatureDrawable(int featureId, @Nullable Drawable drawable) {
        mDialog.setFeatureDrawable(featureId, drawable);
    }

    public void setFeatureDrawableAlpha(int featureId, int alpha) {
        mDialog.setFeatureDrawableAlpha(featureId, alpha);
    }

    public LayoutInflater getLayoutInflater() {
        return mDialog.getLayoutInflater();
    }

    public void setCancelable(boolean flag) {
        mDialog.setCancelable(flag);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void cancel() {
        mDialog.cancel();
    }

    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        mDialog.setOnCancelListener(listener);
    }

    public void setCancelMessage(@Nullable Message msg) {
        mDialog.setCancelMessage(msg);
    }

    public void setOnDismissListener(@Nullable DialogInterface.OnDismissListener listener) {
        mDialog.setOnDismissListener(listener);
    }

    public void setDismissMessage(@Nullable Message msg) {
        mDialog.setDismissMessage(msg);
    }

    public void setVolumeControlStream(int streamType) {
        mDialog.setVolumeControlStream(streamType);
    }

    public int getVolumeControlStream() {
        return mDialog.getVolumeControlStream();
    }

    public void setOnKeyListener(@Nullable DialogInterface.OnKeyListener onKeyListener) {
        mDialog.setOnKeyListener(onKeyListener);
    }

    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {
        mDialog.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    public JsDialog once(String eventName, Object listener) {
        mEmitter.once(eventName, listener);
        return this;
    }

    public JsDialog on(String eventName, Object listener) {
        mEmitter.on(eventName, listener);
        return this;
    }

    public JsDialog addListener(String eventName, Object listener) {
        mEmitter.addListener(eventName, listener);
        return this;
    }

    public boolean emit(String eventName, Object... args) {
        return mEmitter.emit(eventName, args);
    }

    public String[] eventNames() {
        return mEmitter.eventNames();
    }

    public int listenerCount(String eventName) {
        return mEmitter.listenerCount(eventName);
    }

    public Object[] listeners(String eventName) {
        return mEmitter.listeners(eventName);
    }

    public JsDialog prependListener(String eventName, Object listener) {
        mEmitter.prependListener(eventName, listener);
        return this;
    }

    public JsDialog prependOnceListener(String eventName, Object listener) {
        mEmitter.prependOnceListener(eventName, listener);
        return this;
    }

    public JsDialog removeAllListeners() {
        mEmitter.removeAllListeners();
        return this;
    }

    public JsDialog removeAllListeners(String eventName) {
        mEmitter.removeAllListeners(eventName);
        return this;
    }

    public JsDialog removeListener(String eventName, Object listener) {
        mEmitter.removeListener(eventName, listener);
        return this;
    }

    public JsDialog setMaxListeners(int n) {
        mEmitter.setMaxListeners(n);
        return this;
    }

    public int getMaxListeners() {
        return mEmitter.getMaxListeners();
    }

    public static int defaultMaxListeners() {
        return EventEmitter.defaultMaxListeners();
    }


}
