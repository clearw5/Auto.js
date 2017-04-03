package com.stardust.scriptdroid.external.tasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.external.CommonUtils;
import com.stardust.scriptdroid.external.open.RunIntentActivity;
import com.stardust.scriptdroid.external.shortcut.ShortcutActivity;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;

/**
 * Created by Stardust on 2017/3/27.
 */

public class FireSettingReceiver extends AbstractPluginSettingReceiver {

    private static final String TAG = "FireSettingReceiver";

    @Override
    protected boolean isBundleValid(@NonNull Bundle bundle) {
        return CommonUtils.isTaskerBundleValid(bundle);
    }

    @Override
    protected boolean isAsync() {
        return true;
    }

    @Override
    protected void firePluginSetting(@NonNull Context context, @NonNull Bundle bundle) {
        context.startActivity(new Intent(App.getApp(), RunIntentActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(CommonUtils.EXTRA_KEY_PATH, bundle.getString(CommonUtils.EXTRA_KEY_PATH))
                .putExtra(CommonUtils.EXTRA_KEY_PREPARE_SCRIPT, bundle.getString(CommonUtils.EXTRA_KEY_PREPARE_SCRIPT)));
    }
}
