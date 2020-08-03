package org.autojs.autojs.model.script;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PathChecker {
    public static final int CHECK_RESULT_OK = 0;

    private Context mContext;

    public PathChecker(Context context) {
        mContext = context;
    }


    public static int check(final String path) {
        if (TextUtils.isEmpty(path))
            return com.stardust.autojs.R.string.text_path_is_empty;
        if (!new File(path).exists())
            return com.stardust.autojs.R.string.text_file_not_exists;
        return CHECK_RESULT_OK;
    }

    public boolean checkAndToastError(String path) {
        int result = checkWithStoragePermission(path);
        if (result != CHECK_RESULT_OK) {
            Toast.makeText(mContext, mContext.getString(result) + ":" + path, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int checkWithStoragePermission(String path) {
        if (mContext instanceof Activity && !hasStorageReadPermission((Activity) mContext)) {
            return com.stardust.autojs.R.string.text_no_file_rw_permission;
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
