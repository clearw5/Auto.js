package com.stardust.auojs.inrt;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.stardust.auojs.inrt.autojs.AutoJs;
import com.stardust.auojs.inrt.launch.AssetsProjectLauncher;
import com.stardust.autojs.core.console.ConsoleView;
import com.stardust.autojs.core.console.StardustConsole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 11186;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void setupView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ConsoleView consoleView = (ConsoleView) findViewById(R.id.console);
        consoleView.setConsole((StardustConsole) AutoJs.getInstance().getGlobalConsole());

    }


    private void runScript() {
        new Thread(() -> new AssetsProjectLauncher("project", this).launch()).start();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
