package org.autojs.autojs.tool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.stardust.app.OnActivityResultListener;
import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/3/5.
 */

public class ImageSelector implements OnActivityResultListener {

    public interface ImageSelectorCallback {
        void onImageSelected(ImageSelector selector, Uri uri);
    }

    private static final String TAG = ImageSelector.class.getSimpleName();

    private static final int REQUEST_CODE = "LOVE HONMUA".hashCode() >> 16;
    private Activity mActivity;
    private ImageSelectorCallback mCallback;
    private boolean mDisposable;
    private ActivityResultObserver mActivityResultObserver;

    public ImageSelector(Activity activity, ActivityResultObserver activityResultObserver, ImageSelectorCallback callback) {
        activityResultObserver.addListener(REQUEST_CODE, this);
        mActivity = activity;
        mCallback = callback;
        mActivityResultObserver = activityResultObserver;
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
            mActivityResultObserver.removeListener(this);
        }
        if (data == null) {
            mCallback.onImageSelected(this, null);
            return;
        }
        mCallback.onImageSelected(this, data.getData());

    }


}
