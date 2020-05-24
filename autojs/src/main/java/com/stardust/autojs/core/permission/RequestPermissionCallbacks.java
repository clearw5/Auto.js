package com.stardust.autojs.core.permission;

import androidx.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

public class RequestPermissionCallbacks {

    private List<OnRequestPermissionsResultCallback> mCallbacks = new ArrayList<>();


    public void addCallback(OnRequestPermissionsResultCallback callback) {
        mCallbacks.add(callback);
    }


    public boolean removeCallback(OnRequestPermissionsResultCallback callback) {
        return mCallbacks.remove(callback);
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mCallbacks.size() == 0) {
            return false;
        }
        for (OnRequestPermissionsResultCallback callback : mCallbacks) {
            callback.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        return true;
    }
}
