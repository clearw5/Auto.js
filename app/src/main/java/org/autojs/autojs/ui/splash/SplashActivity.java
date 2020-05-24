package org.autojs.autojs.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;

import org.autojs.autojs.R;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.main.MainActivity_;

/**
 * Created by Stardust on 2017/7/7.
 */
public class SplashActivity extends BaseActivity {


    private static final String LOG_TAG = SplashActivity.class.getSimpleName();
    private static final long INIT_TIMEOUT = 800;

    private boolean mAlreadyEnterNextActivity = false;
    private boolean mPaused;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        mHandler.postDelayed(SplashActivity.this::enterNextActivity, INIT_TIMEOUT);
    }

    private void init() {
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            mPaused = false;
            enterNextActivity();
        }
    }

    void enterNextActivity() {
        if (mAlreadyEnterNextActivity)
            return;
        if (mPaused) {
            return;
        }
        mAlreadyEnterNextActivity = true;
        MainActivity_.intent(this).start();
        finish();
    }

}
