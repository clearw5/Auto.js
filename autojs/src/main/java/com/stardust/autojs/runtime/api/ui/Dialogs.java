package com.stardust.autojs.runtime.api.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.autojs.R;
import com.stardust.autojs.runtime.ScriptInterface;
import com.stardust.autojs.runtime.ScriptInterruptedException;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.concurrent.VolatileBox;
import com.stardust.util.ArrayUtils;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2017/5/8.
 */

public class Dialogs {

    private AppUtils mAppUtils;
    private UiHandler mUiHandler;

    public Dialogs(AppUtils appUtils, UiHandler uiHandler) {
        mAppUtils = appUtils;
        mUiHandler = uiHandler;
    }

    @ScriptInterface
    public String rawInput(String title, String prefill) {
        VolatileBox<String> result = new VolatileBox<>(null);
        dialogBuilder()
                .input(null, prefill, true, result)
                .title(title)
                .show();
        return result.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @ScriptInterface
    public void alert(String title, String content) {
        VolatileBox<Void> lock = new VolatileBox<>();
        MaterialDialog.Builder builder = dialogBuilder()
                .dismissListener(lock)
                .title(title)
                .positiveText(R.string.ok);
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        builder.show();
        lock.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @ScriptInterface
    public boolean confirm(String title, String content) {
        VolatileBox<Boolean> result = new VolatileBox<>(false);
        MaterialDialog.Builder builder = dialogBuilder()
                .dismissListener(result)
                .confirm(result)
                .title(title)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel);
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        builder.show();
        return result.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    private Context getContext() {
        return mUiHandler.getContext();
    }

    @ScriptInterface
    public int select(String title, String... items) {
        VolatileBox<Integer> result = new VolatileBox<>(-1);
        dialogBuilder()
                .itemsCallback(result)
                .title(title)
                .items((CharSequence[]) items)
                .show();
        return result.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @ScriptInterface
    public int singleChoice(String title, int selectedIndex, String... items) {
        VolatileBox<Integer> result = new VolatileBox<>(-1);
        dialogBuilder()
                .itemsCallbackSingleChoice(selectedIndex, result)
                .title(title)
                .positiveText(R.string.ok)
                .items((CharSequence[]) items)
                .show();
        return result.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    @ScriptInterface
    public int[] multiChoice(String title, int[] indices, String... items) {
        VolatileBox<Integer[]> result = new VolatileBox<>(new Integer[0]);
        dialogBuilder()
                .itemsCallbackMultiChoice(ArrayUtils.box(indices), result)
                .title(title)
                .positiveText(R.string.ok)
                .items((CharSequence[]) items)
                .show();
        return ArrayUtils.unbox(result.blockedGetOrThrow(ScriptInterruptedException.class));
    }


    private BlockedMaterialDialog.Builder dialogBuilder() {
        Context context = mAppUtils.getCurrentActivity();
        if (context == null) {
            context = getContext();
        }
        return (BlockedMaterialDialog.Builder) new BlockedMaterialDialog.Builder(context, mUiHandler)
                .theme(Theme.LIGHT);
    }
}
