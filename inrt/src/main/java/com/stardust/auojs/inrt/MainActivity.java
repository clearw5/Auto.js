package com.stardust.auojs.inrt;

import android.Manifest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stardust.auojs.inrt.rt.AutoJs;
import com.stardust.autojs.core.console.ConsoleView;
import com.stardust.autojs.core.console.StardustConsole;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFiles;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1209;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        runScript();
    }

    private void setupView() {
        ConsoleView consoleView = new ConsoleView(this);
        consoleView.setConsole((StardustConsole) AutoJs.getInstance().getGlobalConsole());
        setContentView(consoleView);
    }


    private void runScript() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String js = PFiles.read(getAssets().open("script.js"));
                    StringScriptSource source = new StringScriptSource("main", js);
                    AutoJs.getInstance().getScriptEngineService().execute(source);
                } catch (Exception e) {
                    AutoJs.getInstance().getGlobalConsole().log(e);
                }
            }
        }).start();
    }

    protected void checkPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] requestPermissions = getRequestPermissions(permissions);
            if (requestPermissions.length > 0) {
                requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
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
