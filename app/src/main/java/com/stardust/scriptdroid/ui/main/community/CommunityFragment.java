package com.stardust.scriptdroid.ui.main.community;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.webkit.WebView;

import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.ViewPagerFragment;
import com.stardust.util.BackPressedHandler;
import com.stardust.widget.EWebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/8/22.
 */
@EFragment(R.layout.fragment_community)
public class CommunityFragment extends ViewPagerFragment implements BackPressedHandler {


    private static final String POSTS_PAGE_PATTERN = "[\\S\\s]+/topic/[0-9]+/[\\S\\s]+";

    @ViewById(R.id.eweb_view)
    EWebView mEWebView;
    WebView mWebView;

    public CommunityFragment() {
        super(0);
    }

    @AfterViews
    void setUpViews() {
        mWebView = mEWebView.getWebView();
        mWebView.loadUrl("http://www.autojs.org/");
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
        if (isInPostsPage()) {
            mWebView.loadUrl("javascript:$('button[component=\"topic/reply\"]').click()");
        } else {
            mWebView.loadUrl("javascript:$('.new_topic').click()");
        }
    }

    private boolean isInPostsPage() {
        String url = mWebView.getUrl();
        return url.matches(POSTS_PAGE_PATTERN);
    }
}
