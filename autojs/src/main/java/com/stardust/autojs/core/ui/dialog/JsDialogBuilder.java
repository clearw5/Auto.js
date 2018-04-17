package com.stardust.autojs.core.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.Theme;
import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.util.UiHandler;

import java.text.NumberFormat;
import java.util.Collection;

/**
 * Created by Stardust on 2018/4/17.
 */

public class JsDialogBuilder extends MaterialDialog.Builder {

    private final EventEmitter mEmitter;
    private final UiHandler mUiHandler;

    public JsDialogBuilder(@NonNull Context context, ScriptBridges bridges, UiHandler uiHandler) {
        super(context);
        mEmitter = new EventEmitter(bridges);
        this.mUiHandler = uiHandler;
        setUpEvents();
    }

    private void setUpEvents() {
        showListener(dialog -> emit("show", dialog));
        onAny((dialog, which) -> {
            switch (which) {
                case NEUTRAL:
                    emit("neutral", dialog);
                    emit("any", "neutral", dialog);
                    break;
                case NEGATIVE:
                    emit("negative", dialog);
                    emit("any", "negative", dialog);
                    break;
                case POSITIVE:
                    EditText editText = dialog.getInputEditText();
                    if (editText != null) {
                        emit("input", editText.getText().toString());
                    }
                    emit("positive", dialog);
                    emit("any", "positive", dialog);
                    break;
            }
        });
        dismissListener(dialog -> emit("dismiss", dialog));
        cancelListener(dialog -> emit("cancel", dialog));
    }

    @Override
    public MaterialDialog build() {
        return new JsDialog(this, mEmitter, mUiHandler);
    }

    public EventEmitter once(String eventName, Object listener) {
        return mEmitter.once(eventName, listener);
    }

    public EventEmitter on(String eventName, Object listener) {
        return mEmitter.on(eventName, listener);
    }

    public EventEmitter addListener(String eventName, Object listener) {
        return mEmitter.addListener(eventName, listener);
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

    public EventEmitter prependListener(String eventName, Object listener) {
        return mEmitter.prependListener(eventName, listener);
    }

    public EventEmitter prependOnceListener(String eventName, Object listener) {
        return mEmitter.prependOnceListener(eventName, listener);
    }

    public EventEmitter removeAllListeners() {
        return mEmitter.removeAllListeners();
    }

    public EventEmitter removeAllListeners(String eventName) {
        return mEmitter.removeAllListeners(eventName);
    }

    public EventEmitter removeListener(String eventName, Object listener) {
        return mEmitter.removeListener(eventName, listener);
    }

    public EventEmitter setMaxListeners(int n) {
        return mEmitter.setMaxListeners(n);
    }

    public int getMaxListeners() {
        return mEmitter.getMaxListeners();
    }

    public static int defaultMaxListeners() {
        return EventEmitter.defaultMaxListeners();
    }

}
