package com.stardust.scriptdroid.ui.main.doc;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
@EFragment(R.layout.fragment_online_docs)
public class OnlineDocsFragment extends ViewPagerFragment implements BackPressedHandler {

    @ViewById(R.id.eweb_view)
    EWebView mEWebView;
    WebView mWebView;


    public OnlineDocsFragment() {
        super(ROTATION_GONE);
    }

    @AfterViews
    void setUpViews() {
        mWebView = mEWebView.getWebView();
        mWebView.loadUrl("https://hyb1996.github.io/AutoJs-Docs/");
    }


    @Override
    public void onResume() {
        super.onResume();
        ((BackPressedHandler.HostActivity) getActivity())
                .getBackPressedObserver()
                .registerHandlerAtFront(this);
    }

    @Override
    public void onPause() {
        super.onPause();
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
