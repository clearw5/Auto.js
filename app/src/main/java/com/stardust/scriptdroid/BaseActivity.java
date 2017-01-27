package com.stardust.scriptdroid;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.stream.Stream;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

/**
 * Created by Stardust on 2017/1/23.
 */

public class BaseActivity extends AppCompatActivity {


    @SuppressWarnings("unchecked")
    public <T extends View> T $(int resId) {
        return (T) findViewById(resId);
    }

    protected void checkPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] requestPermissions = Stream.of(permissions).filter(permission -> checkSelfPermission(permission) == PERMISSION_DENIED).toArray(String[]::new);

            if (requestPermissions.length > 0)
                requestPermissions(requestPermissions, 0);
        }
    }

}
