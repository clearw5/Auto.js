package com.stardust.autojs.core.permission;

import android.support.annotation.NonNull;

public interface OnRequestPermissionsResultCallback {

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
