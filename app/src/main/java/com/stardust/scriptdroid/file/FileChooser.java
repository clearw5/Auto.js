package com.stardust.scriptdroid.file;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Stardust on 2017/1/23.
 */

public class FileChooser {

    public interface FileManagerNotFoundHandler {
        void handle(ActivityNotFoundException e, String mimeType);
    }

    public interface OnFileChoseListener {
        void onFileChose(InputStream inputStream);
    }

    private static final int FILE_CHOOSE = 1209;
    private Activity mActivity;

    private OnFileChoseListener mOnFileChoseListener;

    public FileChooser(Activity activity) {
        mActivity = activity;
    }

    public void setOnFileChoseListener(OnFileChoseListener onFileChoseListener) {
        mOnFileChoseListener = onFileChoseListener;
    }


    public void startFileManagerToChoose(String mimeType, FileManagerNotFoundHandler handler) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            mActivity.startActivityForResult(Intent.createChooser(intent, "选择一个文件"), FILE_CHOOSE);
        } catch (ActivityNotFoundException ex) {
            handler.handle(ex, mimeType);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = mActivity.getContentResolver();
            try {
                InputStream inputStream = cr.openInputStream(uri);
                mOnFileChoseListener.onFileChose(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
