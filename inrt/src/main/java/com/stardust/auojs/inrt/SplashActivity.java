package com.stardust.auojs.inrt;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.stardust.auojs.inrt.launch.AssetsProjectLauncher;
import com.stardust.auojs.inrt.launch.GlobalProjectLauncher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Stardust on 2018/2/2.
 */

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 11186;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Pref.isFirstUsing()) {
            main();
            return;
        }
        setContentView(R.layout.activity_splash);
        TextView slug = (TextView) findViewById(R.id.slug);
        slug.setTypeface(Typeface.createFromAsset(getAssets(), "roboto_medium.ttf"));
        new Handler().postDelayed(this::main, 2500);
    }

    private void main() {
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }


    private void runScript() {
        new Thread(() -> GlobalProjectLauncher.getInstance().launch(this)).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        runScript();
    }

    protected void checkPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] requestPermissions = getRequestPermissions(permissions);
            if (requestPermissions.length > 0) {
                requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
            } else {
                runScript();
            }
        } else {
            int[] grantResults = new int[permissions.length];
            Arrays.fill(grantResults, PERMISSION_GRANTED);
            onRequestPermissionsResult(PERMISSION_REQUEST_CODE, permissions, grantResults);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private String[] getRequestPermissions(String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PERMISSION_DENIED) {
                list.add(permission);
            }
        }
        return list.toArray(new String[list.size()]);
    }

}

