package com.stardust.autojs.runtime.api;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.autojs.R;
import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.autojs.core.ui.BlockedMaterialDialog;
import com.stardust.autojs.runtime.ScriptBridges;
import com.stardust.util.ArrayUtils;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2017/5/8.
 */

public class Dialogs {

    private AppUtils mAppUtils;
    private UiHandler mUiHandler;
    private ContextThemeWrapper mThemeWrapper;
    private ScriptBridges mScriptBridges;

    @ScriptVariable
    public final NonUiDialogs nonUiDialogs = new NonUiDialogs();

    public Dialogs(AppUtils appUtils, UiHandler uiHandler, ScriptBridges scriptBridges) {
        mAppUtils = appUtils;
        mUiHandler = uiHandler;
        mScriptBridges = scriptBridges;
    }

    @ScriptInterface
    public Object rawInput(String title, String prefill, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .input(null, prefill, true)
                .title(title))
                .showAndGet();
    }


    @ScriptInterface
    public Object alert(String title, String content, Object callback) {
        MaterialDialog.Builder builder = dialogBuilder(callback)
                .alert()
                .title(title)
                .positiveText(R.string.ok);
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        return ((BlockedMaterialDialog.Builder) builder).showAndGet();
    }

    @ScriptInterface
    public Object confirm(String title, String content, Object callback) {
        MaterialDialog.Builder builder = dialogBuilder(callback)
                .confirm()
                .title(title)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel);
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        return ((BlockedMaterialDialog.Builder) builder).showAndGet();
    }

    private Context getContext() {
        if (mThemeWrapper != null)
            return mThemeWrapper;
        mThemeWrapper = new ContextThemeWrapper(mUiHandler.getContext().getApplicationContext(), R.style.Theme_AppCompat_Light);
        return mThemeWrapper;
    }

    @ScriptInterface
    public Object select(String title, String[] items, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .itemsCallback()
                .title(title)
                .items((CharSequence[]) items))
                .showAndGet();
    }

    private String[] getItems(Object[] args) {
        int len = 0;
        if (args.length > 1) {
            if (args[args.length - 1] instanceof CharSequence) {
                len = args.length;
            } else {
                len = args.length - 1;
            }
        }
        String[] items = new String[len];
        for (int i = 0; i < len; i++) {
            items[i] = args[i] == null ? null : args[i].toString();
        }
        return items;
    }

    private Object getCallback(Object[] args) {
        if (args.length > 1 && !(args[args.length - 1] instanceof CharSequence)) {
            return args[args.length - 1];
        }
        return null;
    }

    @ScriptInterface
    public Object singleChoice(String title, int selectedIndex, String[] items, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .itemsCallbackSingleChoice(selectedIndex)
                .title(title)
                .positiveText(R.string.ok)
                .items((CharSequence[]) items))
                .showAndGet();
    }

    @ScriptInterface
    public Object multiChoice(String title, int[] indices, String[] items, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .itemsCallbackMultiChoice(ArrayUtils.box(indices))
                .title(title)
                .positiveText(R.string.ok)
                .items((CharSequence[]) items))
                .showAndGet();
    }


    private BlockedMaterialDialog.Builder dialogBuilder(Object callback) {
        Context context = mAppUtils.getCurrentActivity();
        if (context == null || ((Activity) context).isFinishing()) {
            context = getContext();
        }
        return (BlockedMaterialDialog.Builder) new BlockedMaterialDialog.Builder(context, mUiHandler, mScriptBridges, callback)
                .theme(Theme.LIGHT);
    }

    public class NonUiDialogs {

        public String rawInput(String title, String prefill, Object callback) {
            return (String) Dialogs.this.rawInput(title, prefill, callback);
        }

        @ScriptInterface
        public boolean confirm(String title, String content, Object callback) {
            return (boolean) Dialogs.this.confirm(title, content, callback);
        }

        @ScriptInterface
        public int select(String title, String[] items, Object callback) {
            return (Integer) Dialogs.this.select(title, items, callback);
        }

        @ScriptInterface
        public int singleChoice(String title, int selectedIndex, String[] items, Object callback) {
            return (int) Dialogs.this.singleChoice(title, selectedIndex, items, callback);
        }

        @ScriptInterface
        public int[] multiChoice(String title, int[] indices, String[] items, Object callback) {
            return (int[]) Dialogs.this.multiChoice(title, indices, items, callback);
        }

        @ScriptInterface
        public Object alert(String title, String content, Object callback) {
            return Dialogs.this.alert(title, content, callback);
        }

    }
}
