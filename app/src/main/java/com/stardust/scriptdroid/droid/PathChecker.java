package com.stardust.scriptdroid.droid;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.stardust.scriptdroid.R;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PathChecker {
    public static final int CHECK_RESULT_OK = 0;

    private Activity mActivity;

    public PathChecker(Activity activity) {
        mActivity = activity;
    }


    public static int check(final String path) {
        if (TextUtils.isEmpty(path))
            return R.string.text_path_is_empty;
        if (!new File(path).exists())
            return R.string.text_file_not_exists;
        return CHECK_RESULT_OK;
    }

    public boolean checkAndToastError(String path) {
        int result = checkWithStoragePermission(path);
        if (result != CHECK_RESULT_OK) {
            Toast.makeText(mActivity, result, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int checkWithStoragePermission(String path) {
        if (!hasStorageReadPermission(mActivity)) {
            return R.string.text_no_file_rw_permission;
        }
        return check(path);
    }

    private static boolean hasStorageReadPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    activity.checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        }
        return true;
    }


}
