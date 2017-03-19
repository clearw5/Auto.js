package com.stardust.scriptdroid.tool;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.scriptdroid.R;

/**
 * Created by Stardust on 2017/3/5.
 */

public class ImageSelector implements OnActivityResultDelegate {

    public interface ImageSelectorCallback {
        void onImageSelected(ImageSelector selector, String path);
    }

    private static final int REQUEST_CODE = "LOVE EATING".hashCode() >> 16;
    private Activity mActivity;
    private ImageSelectorCallback mCallback;

    public ImageSelector(Activity activity, Intermediary intermediary, ImageSelectorCallback callback) {
        intermediary.addDelegate(REQUEST_CODE, this);
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
        if (data == null)
            return;
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            mCallback.onImageSelected(this, filePath);
        }


    }


}
