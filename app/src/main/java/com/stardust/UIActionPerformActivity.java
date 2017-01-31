package com.stardust;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.droid.runtime.DroidRuntime;


/**
 * Created by Stardust on 2017/1/31.
 */

public class UIActionPerformActivity extends AppCompatActivity {


    private static volatile Runnable action;

    public static void performAction(Runnable action) {
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
        action.run();
    }




}
