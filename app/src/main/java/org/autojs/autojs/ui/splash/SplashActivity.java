package org.autojs.autojs.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.xcy8.ads.listener.LoadAdListener;
import com.xcy8.ads.view.FullScreenAdView;
import com.xcy8.ads.view.skipview.OnFullScreenListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.Constants;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.main.MainActivity_;

import java.util.Random;

/**
 * Created by Stardust on 2017/7/7.
 */
@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    public static final String NOT_START_MAIN_ACTIVITY = "notStartMainActivity";
    public static final String FORCE_SHOW_AD = "forceShowAd";

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    @ViewById(R.id.logo)
    ImageView mLogo;

    private boolean mCanEnterNextActivity = false;
    private boolean mNotStartMainActivity;
    private boolean mAlreadyEnterNextActivity = false;
    private boolean mAdLoading = false;

    private Handler mHandler;

    @ViewById(R.id.full_screen_view)
    FullScreenAdView mFullScreenAdView;

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
    }

    @AfterViews
    void afterViews(){
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
