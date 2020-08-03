package org.autojs.autojs.ui.doc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.widget.EWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Stardust on 2017/10/24.
 */

public class ManualDialog {

    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.eweb_view)
    EWebView mEWebView;

    @BindView(R.id.pin_to_left)
    View mPinToLeft;

    Dialog mDialog;
    private Context mContext;

    public ManualDialog(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.floating_manual_dialog, null);
        ButterKnife.bind(this, view);
        mDialog = new MaterialDialog.Builder(context)
                .customView(view, false)
                .build();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public ManualDialog title(String title) {
        mTitle.setText(title);
        return this;
    }

    public ManualDialog url(String url) {
        mEWebView.getWebView().loadUrl(url);
        return this;
    }

    public ManualDialog pinToLeft(View.OnClickListener listener) {
        mPinToLeft.setOnClickListener(v -> {
            mDialog.dismiss();
            listener.onClick(v);
        });
        return this;
    }

    public ManualDialog show() {
        mDialog.show();
        return this;
    }

    @OnClick(R.id.close)
    void close() {
        mDialog.dismiss();
    }

    @OnClick(R.id.fullscreen)
    void viewInNewActivity() {
        mDialog.dismiss();
        DocumentationActivity_.intent(mContext)
                .extra(DocumentationActivity.EXTRA_URL, mEWebView.getWebView().getUrl())
                .start();
    }

}
