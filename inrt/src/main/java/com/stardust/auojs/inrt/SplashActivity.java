package com.stardust.auojs.inrt;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Stardust on 2018/2/2.
 */

public class SplashActivity extends AppCompatActivity {


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
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

