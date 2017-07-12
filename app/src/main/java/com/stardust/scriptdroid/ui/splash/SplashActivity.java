package com.stardust.scriptdroid.ui.splash;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.stardust.scriptdroid.Constants;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

/**
 * Created by Stardust on 2017/7/7.
 */
@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    public static final String NOT_START_MAIN_ACTIVITY = "Eating...I...really...love...you...";
    public static final String FORCE_SHOW_AD = "I will be better but can you take just a glance at me...";

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    @ViewById(R.id.logo)
    ImageView mLogo;

    @ViewById(R.id.skip_view)
    TextView mSkipView;

    @ViewById(R.id.ad_container)
    View mAdContainer;

    @ViewById(R.id.ad)
    FrameLayout mAd;

    private SplashAD mSplashAD;
    private boolean mCanEnterNextActivity = false;
    private boolean mNotStartMainActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotStartMainActivity = getIntent().getBooleanExtra(NOT_START_MAIN_ACTIVITY, false);
        boolean forceShowAd = getIntent().getBooleanExtra(FORCE_SHOW_AD, false);
        if (!forceShowAd && !Pref.shouldShowAd()) {
            enterNextActivity();
        }
    }

    @AfterViews
    void setUpViews() {
        fetchSplashAD();
    }

    void enterNextActivity() {
        if (!mNotStartMainActivity)
            MainActivity_.intent(this).start();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterNextActivityIfNeeded();
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

    private void fetchSplashAD() {
        mSplashAD = new SplashAD(this, mAd, mSkipView, Constants.APP_ID, Constants.SPLASH_POS_ID, new SplashADListener() {
            @Override
            public void onADDismissed() {
                Log.v(LOG_TAG, "onADDismissed");
                enterNextActivityIfNeeded();
            }

            @Override
            public void onNoAD(int errno) {
                Log.e(LOG_TAG, "onNoAD: " + errno);
                enterNextActivity();
            }

            @Override
            public void onADPresent() {
                Log.v(LOG_TAG, "onADPresent");
                mSkipView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onADClicked() {
                Log.v(LOG_TAG, "onADClicked");
            }

            @Override
            public void onADTick(long l) {
                mSkipView.setText(String.format(Locale.getDefault(), getString(R.string.format_skip), Math.round(l / 1000f)));
            }
        }, 0);
    }
}
