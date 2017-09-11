package com.stardust.scriptdroid.ui.main.community;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.webkit.WebView;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;
import com.stardust.util.BackPressedHandler;
import com.stardust.widget.EWebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Stardust on 2017/8/22.
 */
@EFragment(R.layout.fragment_community)
public class CommunityFragment extends ViewPagerFragment implements BackPressedHandler {


    @ViewById(R.id.eweb_view)
    EWebView mEWebView;
    WebView mWebView;

    public CommunityFragment() {
        super(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((BackPressedHandler.HostActivity) getActivity())
                .getBackPressedObserver()
                .registerHandler(this);
    }

    @AfterViews
    void setUpViews() {
        mWebView = mEWebView.getWebView();
        mWebView.loadUrl("http://39.108.231.37/");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((BackPressedHandler.HostActivity) getActivity())
                .getBackPressedObserver()
                .unregisterHandler(this);
    }

    @Override
    public boolean onBackPressed(Activity activity) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }


    @Override
    protected void onFabClick(FloatingActionButton fab) {

    }
}
