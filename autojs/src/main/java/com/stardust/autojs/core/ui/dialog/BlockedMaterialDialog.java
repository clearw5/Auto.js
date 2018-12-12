package com.stardust.autojs.core.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.Nullable;

import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.autojs.rhino.continuation.Continuation;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.util.ArrayUtils;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2017/5/8.
 */

public class BlockedMaterialDialog extends MaterialDialog {

    protected BlockedMaterialDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    @Override
    public void show() {
        if (!isActivityContext(getContext())) {
            int type;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            getWindow().setType(type);
        }
        super.show();
    }

    private boolean isActivityContext(Context context) {
        if (context == null)
            return false;
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing();
        }
        if (context instanceof ContextWrapper) {
            return isActivityContext(((ContextWrapper) context).getBaseContext());
        }
        return false;
    }


    public static class Builder extends MaterialDialog.Builder {

        private VolatileDispose<Object> mResultBox;
        private UiHandler mUiHandler;
        private Object mCallback;
        private ScriptBridges mScriptBridges;
        private boolean mNotified = false;

        public Builder(Context context, ScriptRuntime runtime, Object callback) {
            super(context);
            super.theme(Theme.LIGHT);
            mUiHandler = runtime.uiHandler;
            mScriptBridges = runtime.bridges;
            mCallback = callback;
            if (Looper.getMainLooper() != Looper.myLooper()) {
                mResultBox = new VolatileDispose<>();
            }
        }

        public MaterialDialog.Builder input(@Nullable CharSequence hint, @Nullable CharSequence prefill, boolean allowEmptyInput) {
            super.input(hint, prefill, allowEmptyInput, (dialog, input) -> setAndNotify(input.toString()));
            cancelListener(dialog -> setAndNotify(null));
            return this;
        }

        private void setAndNotify(Object r) {
            if (mNotified) {
                return;
            }
            mNotified = true;
            if (mCallback != null) {
                mScriptBridges.callFunction(mCallback, null, new Object[]{r});
            }
            if (mResultBox != null) {
                mResultBox.setAndNotify(r);
            }
        }

        private void setAndNotify(int r) {
            if (mNotified) {
                return;
            }
            mNotified = true;
            if (mCallback != null) {
                mScriptBridges.callFunction(mCallback, null, new int[]{r});
            }
            if (mResultBox != null) {
                mResultBox.setAndNotify(r);
            }
        }

        private void setAndNotify(boolean r) {
            if (mNotified) {
                return;
            }
            mNotified = true;
            if (mCallback != null) {
                mScriptBridges.callFunction(mCallback, null, new boolean[]{r});
            }
            if (mResultBox != null) {
                mResultBox.setAndNotify(r);
            }
        }

        public Builder alert() {
            dismissListener(dialog -> setAndNotify(null));
            onAny((dialog, which) -> setAndNotify(null));
            return this;
        }

        public Builder confirm() {
            dismissListener(dialog -> setAndNotify(false));
            onAny((dialog, which) -> {
                if (which == DialogAction.POSITIVE) {
                    setAndNotify(true);
                } else {
                    setAndNotify(false);
                }
            });
            return this;
        }

        public MaterialDialog.Builder itemsCallback() {
            dismissListener(dialog -> setAndNotify(-1));
            super.itemsCallback((dialog, itemView, position, text) -> setAndNotify(position));
            return this;
        }

        public MaterialDialog.Builder itemsCallbackMultiChoice(@Nullable Integer[] selectedIndices) {
            dismissListener(dialog -> setAndNotify(new int[0]));
            super.itemsCallbackMultiChoice(selectedIndices, (dialog, which, text) -> {
                setAndNotify(ArrayUtils.unbox(which));
                return true;
            });
            return this;
        }

        public MaterialDialog.Builder itemsCallbackSingleChoice(int selectedIndex) {
            dismissListener(dialog -> setAndNotify(-1));
            super.itemsCallbackSingleChoice(selectedIndex, (dialog, itemView, which, text) -> {
                setAndNotify(which);
                return true;
            });
            return this;
        }


        public Object showAndGet() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.show();
            } else {
                mUiHandler.post(Builder.super::show);
            }
            if (mResultBox != null) {
                return mResultBox.blockedGetOrThrow(ScriptInterruptedException.class);
            } else {
                return null;
            }
        }

        @Override
        public MaterialDialog build() {
            return new BlockedMaterialDialog(this);
        }

    }
}
