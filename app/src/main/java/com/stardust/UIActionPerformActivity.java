package com.stardust;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;


/**
 * Created by Stardust on 2017/1/31.
 */

public class UIActionPerformActivity extends AppCompatActivity {


    private static volatile UIAction action;

    public static void performAction(UIAction action) {
        if (UIActionPerformActivity.action != null) {
            return;
        }
        UIActionPerformActivity.action = action;
        App.getApp().startActivity(new Intent(App.getApp(), UIActionPerformActivity.class));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DroidRuntime.setContext(this);
        action.setActivity(this).run();
    }


    public abstract static class UIAction implements Runnable {

        protected Activity mActivity;

        public UIAction setActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

    }


}
