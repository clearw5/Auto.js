package com.stardust.scriptdroid.ui.main.doc;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import com.stardust.scriptdroid.R;
import com.stardust.util.BackPressedHandler;
import com.stardust.widget.EWebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Stardust on 2017/8/22.
 */
@EFragment(R.layout.fragment_online_docs)
public class OnlineDocsFragment extends Fragment implements BackPressedHandler {

    @ViewById(R.id.eweb_view)
    EWebView mEWebView;
    WebView mWebView;

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
        mWebView.loadUrl("https://hyb1996.github.io/AutoJs-Docs/");
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
}
