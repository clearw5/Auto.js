package com.stardust.auojs.inrt;

import android.Manifest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stardust.auojs.inrt.rt.AutoJs;
import com.stardust.autojs.script.StringScriptSource;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        try {
            InputStream is = getAssets().open("script.js");
            byte[] data = new byte[is.available()];
            is.read(data);
            String js = new String(data);
            StringScriptSource source = new StringScriptSource("<script>", js);
            AutoJs.getInstance().getScriptEngineService().execute(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11124);
        }
    }
}
