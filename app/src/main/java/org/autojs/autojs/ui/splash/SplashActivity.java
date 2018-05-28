package org.autojs.autojs.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
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

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.Constants;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.main.MainActivity_;

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

    private boolean mCanEnterNextActivity = false;
    private boolean mNotStartMainActivity;
    private boolean mAlreadyEnterNextActivity = false;
    private boolean mAdLoading = false;

    private Handler mHandler;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mNotStartMainActivity = getIntent().getBooleanExtra(NOT_START_MAIN_ACTIVITY, false);
        boolean forceShowAd = getIntent().getBooleanExtra(FORCE_SHOW_AD, false);
        if (!forceShowAd && !Pref.shouldShowAd()) {
            enterNextActivity();
            return;
        }
        fetchSplashAD();
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
        mAdLoading = true;
        mHandler.postDelayed(() -> {
            if (mAdLoading)
                enterNextActivity();
        }, 3000);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(BuildConfig.DEBUG ? Constants.ADMOB_INTERSTITIAL_TEST_ID : Constants.ADMOB_INTERSTITIAL_ID);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdLoading = false;
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                enterNextActivity();
            }

            @Override
            public void onAdClicked() {
                enterNextActivity();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                mAdLoading = false;
                enterNextActivity();
                Log.d(Constants.LOG_TAG_ADMOB, "Fail to load interstitial ad: " + i);
            }

        });
        mInterstitialAd.loadAd(buildAdRequest());
    }

    private AdRequest buildAdRequest() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            builder.addTestDevice("774E105820188FA387B617ECD279B167");
        }
        return builder.build();
    }
}
