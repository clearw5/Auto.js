package com.stardust.auojs.inrt;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.auojs.inrt.launch.GlobalProjectLauncher;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

/**
 * Created by Stardust on 2018/2/2.
 */

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 11186;
    private static final long INIT_TIMEOUT = 2500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView slug = findViewById(R.id.slug);
        slug.setTypeface(Typeface.createFromAsset(getAssets(), "roboto_medium.ttf"));
        if (!Pref.isFirstUsing()) {
            main();
        }else {
            new Handler().postDelayed(SplashActivity.this::main, INIT_TIMEOUT);
        }
    }

    private void main() {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }


    private void runScript() {
        new Thread(() -> {
            try {
                GlobalProjectLauncher.getInstance().launch(this);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SplashActivity.this, LogActivity.class));
                    AutoJs.getInstance().getGlobalConsole().printAllStackTrace(e);
                });
            }
        }).start();
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
            runScript();
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

