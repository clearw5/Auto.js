package com.stardust.autojs.runtime.api.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.concurrent.VolatileBox;
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
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        super.show();
    }

    private boolean isActivityContext(Context context) {
        if (context == null)
            return false;
        if (context instanceof Activity)
            return true;
        if (context instanceof ContextWrapper) {
            return isActivityContext(((ContextWrapper) context).getBaseContext());
        }
        return false;
    }


    public static class Builder extends MaterialDialog.Builder {

        private UiHandler mUiHandler;

        public Builder(Context context, UiHandler uiHandler) {
            super(context);
            super.theme(Theme.LIGHT);
            mUiHandler = uiHandler;
        }

        public MaterialDialog.Builder input(@Nullable CharSequence hint, @Nullable CharSequence prefill, boolean allowEmptyInput, final VolatileBox<String> result) {
            dismissListener(result);
            super.input(hint, prefill, allowEmptyInput, new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    result.set(input.toString());
                    synchronized (result) {
                        result.notify();
                    }
                }
            });
            return this;
        }

        public Builder confirm(final VolatileBox<Boolean> result) {
            onAny(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (which == DialogAction.POSITIVE) {
                        result.setAndNotify(true);
                    } else {
                        result.setAndNotify(false);
                    }
                }
            });
            return this;
        }

        public MaterialDialog.Builder itemsCallback(final VolatileBox<Integer> result) {
            dismissListener(result);
            super.itemsCallback(new ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                    result.setAndNotify(position);
                }
            });
            return this;
        }

        public MaterialDialog.Builder itemsCallbackMultiChoice(@Nullable Integer[] selectedIndices, final VolatileBox<Integer[]> result) {
            dismissListener(result);
            super.itemsCallbackMultiChoice(selectedIndices, new ListCallbackMultiChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                    result.setAndNotify(which);
                    return true;
                }
            });
            return this;
        }

        public MaterialDialog.Builder itemsCallbackSingleChoice(int selectedIndex, final VolatileBox<Integer> result) {
            dismissListener(result);
            super.itemsCallbackSingleChoice(selectedIndex, new ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    result.setAndNotify(which);
                    return true;
                }
            });
            return this;
        }

        public Builder dismissListener(final VolatileBox<?> result) {
            super.dismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    synchronized (result) {
                        result.notify();
                    }
                }
            });
            return this;
        }

        @Override
        public MaterialDialog show() {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Builder.super.show();
                }
            });
            return null;
        }

        @Override
        public MaterialDialog build() {
            return new BlockedMaterialDialog(this);
        }

    }
}
