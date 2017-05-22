package com.stardust.scriptdroid.autojs.api;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.runtime.api.image.ScreenCaptureRequester;
import com.stardust.scriptdroid.ui.BaseActivity;

/**
 * Created by Stardust on 2017/5/22.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenCaptureRequestActivity extends BaseActivity {


    private static ScreenCaptureRequester.Callback sCallback;

    public static void request(Context context, ScreenCaptureRequester.Callback callback) {
        if (sCallback != null) {
            return;
        }
        sCallback = callback;
        context.startActivity(new Intent(context, ScreenCaptureRequestActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private OnActivityResultDelegate.Mediator mOnActivityResultDelegateMediator = new OnActivityResultDelegate.Mediator();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenCaptureRequester requester = new ScreenCaptureRequester.ActivityScreenCaptureRequester(mOnActivityResultDelegateMediator, this);
        requester.setOnActivityResultCallback(sCallback);
        requester.request();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mOnActivityResultDelegateMediator.onActivityResult(requestCode, resultCode, data);
        finish();
        sCallback = null;
    }

}
