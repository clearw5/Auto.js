package com.stardust.scriptdroid.tool;

import android.app.Activity;
import android.content.Intent;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.scriptdroid.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Stardust on 2017/3/5.
 */

public class ImageSelector implements OnActivityResultDelegate {

    public interface ImageSelectorCallback {
        void onImageSelected(ImageSelector selector, InputStream path);
    }

    private static final String TAG = ImageSelector.class.getSimpleName();

    private static final int REQUEST_CODE = "LOVE EATING".hashCode() >> 16;
    private Activity mActivity;
    private ImageSelectorCallback mCallback;

    public ImageSelector(Activity activity, OnActivityResultDelegate.Mediator mediator, ImageSelectorCallback callback) {
        mediator.addDelegate(REQUEST_CODE, this);
        mActivity = activity;
        mCallback = callback;
    }

    public void select() {
        mActivity.startActivityForResult(Intent.createChooser(
                new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), mActivity.getString(R.string.text_select_image)),
                REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            mCallback.onImageSelected(this, null);
            return;
        }
        try {
            InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
            mCallback.onImageSelected(this, inputStream);
        } catch (FileNotFoundException e) {
            mCallback.onImageSelected(this, null);
        }

    }


}
