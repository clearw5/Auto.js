package org.autojs.autojs.tool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.stardust.app.OnActivityResultDelegate;
import org.autojs.autojs.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Stardust on 2017/3/5.
 */

public class ImageSelector implements OnActivityResultDelegate {

    public interface ImageSelectorCallback {
        void onImageSelected(ImageSelector selector, Uri uri);
    }

    private static final String TAG = ImageSelector.class.getSimpleName();

    private static final int REQUEST_CODE = "LOVE HONMUA".hashCode() >> 16;
    private Activity mActivity;
    private ImageSelectorCallback mCallback;
    private boolean mDisposable;
    private Mediator mMediator;

    public ImageSelector(Activity activity, OnActivityResultDelegate.Mediator mediator, ImageSelectorCallback callback) {
        mediator.addDelegate(REQUEST_CODE, this);
        mActivity = activity;
        mCallback = callback;
        mMediator = mediator;
    }

    public void select() {
        mActivity.startActivityForResult(Intent.createChooser(
                new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), mActivity.getString(R.string.text_select_image)),
                REQUEST_CODE);
    }

    public ImageSelector disposable() {
        mDisposable = true;
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mDisposable) {
            mMediator.removeDelegate(this);
        }
        if (data == null) {
            mCallback.onImageSelected(this, null);
            return;
        }
        mCallback.onImageSelected(this, data.getData());

    }


}
