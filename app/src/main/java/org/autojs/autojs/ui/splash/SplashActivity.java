package org.autojs.autojs.ui.splash;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;

import com.xcy8.ads.listener.LoadAdListener;
import com.xcy8.ads.view.FullScreenAdView;

import org.autojs.autojs.Constants;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.main.MainActivity_;

import java.util.Random;

/**
 * Created by Stardust on 2017/7/7.
 */
public class SplashActivity extends BaseActivity {

    public static final String NOT_START_MAIN_ACTIVITY = "notStartMainActivity";
    public static final String FORCE_SHOW_AD = "forceShowAd";

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();
    private static final long INIT_TIMEOUT = 500;


    private boolean mCanEnterNextActivity = false;
    private boolean mNotStartMainActivity;
    private boolean mAlreadyEnterNextActivity = false;
    private boolean mAdLoading = false;

    private Handler mHandler;

    FullScreenAdView mFullScreenAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        boolean forceShowAd = getIntent().getBooleanExtra(FORCE_SHOW_AD, false);
        if (!forceShowAd && !Pref.shouldShowAd()) {
            mFullScreenAdView.setVisibility(View.INVISIBLE);
        } else {
            if (checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                fetchSplashAD();
            }
        }
        mHandler.postDelayed(SplashActivity.this::enterNextActivity, INIT_TIMEOUT);
    }

    private void init() {
        setContentView(R.layout.activity_splash);
        mFullScreenAdView = findViewById(R.id.full_screen_view);
        mHandler = new Handler();
        mNotStartMainActivity = getIntent().getBooleanExtra(NOT_START_MAIN_ACTIVITY, false);
    }

    void enterNextActivity() {
        if (mAlreadyEnterNextActivity)
            return;
        mAlreadyEnterNextActivity = true;
        if (!mNotStartMainActivity)
            MainActivity_.intent(this).start();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //enterNextActivityIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCanEnterNextActivity = false;
    }

    private void enterNextActivityIfNeeded() {
        if (mCanEnterNextActivity) {
            enterNextActivity();
            return;
        }
        mCanEnterNextActivity = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fetchSplashAD();
    }

    private void fetchSplashAD() {
        mAdLoading = true;
        mFullScreenAdView.setFullScreenListener(this::enterNextActivity);
        mFullScreenAdView.setLoadAdListener(new LoadAdListener() {
            @Override
            public void onSuccess() {
                mAdLoading = false;
                Log.d(LOG_TAG, "onAdLoadSuccess");
            }

            @Override
            public void onFailure(String s) {
                mAdLoading = false;
                Log.e(LOG_TAG, "onAdLoadFailure: " + s);
                enterNextActivity();
            }
        });
        mHandler.postDelayed(() -> {
            if (mAdLoading) {
                enterNextActivity();
            }
        }, 2000);
        mFullScreenAdView.loadAd(getAdId(), false);
    }

    private String getAdId() {
        int type = Pref.getAdType();
        int id = type >= 1 && type <= Constants.UMENG_IDS.length ? Constants.UMENG_IDS[type - 1]
                : Constants.UMENG_IDS[new Random().nextInt(Constants.UMENG_IDS.length)];
        return String.valueOf(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFullScreenAdView.clean();
    }
}
