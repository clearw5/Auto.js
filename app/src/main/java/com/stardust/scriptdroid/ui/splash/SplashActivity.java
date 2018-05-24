package com.stardust.scriptdroid.ui.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.stardust.scriptdroid.BuildConfig;
import com.stardust.scriptdroid.Constants;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.main.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

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
    AdView mAd;

    private boolean mCanEnterNextActivity = false;
    private boolean mNotStartMainActivity;

    private InterstitialAd mInterstitialAd;

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

    @Click(R.id.skip_view)
    void skip() {
        enterNextActivity();
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
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(BuildConfig.DEBUG ? Constants.ADMOB_INTERSTITIAL_TEST_ID : Constants.ADMOB_INTERSTITIAL_ID);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(Constants.LOG_TAG_ADMOB, "Fail to load interstitial ad: " + i);
            }

        });
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());
        mAd.setAdListener(new AdListener() {

            @Override
            public void onAdClicked() {
                enterNextActivity();
                Log.d(Constants.LOG_TAG_ADMOB, "Ad clicked");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(Constants.LOG_TAG_ADMOB, "Fail to load banner ad: " + i);
            }

            @Override
            public void onAdClosed() {
                enterNextActivity();
            }
        });
        if (BuildConfig.DEBUG) {
            mAd.loadAd(new AdRequest.Builder()
                    .addTestDevice("774E105820188FA387B617ECD279B167")
                    .build());
        } else {
            mAd.loadAd(new AdRequest.Builder()
                    .build());
        }

    }
}
