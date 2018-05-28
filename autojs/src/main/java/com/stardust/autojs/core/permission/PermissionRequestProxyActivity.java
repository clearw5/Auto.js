package com.stardust.autojs.core.permission;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public interface PermissionRequestProxyActivity {

    void addRequestPermissionsCallback(OnRequestPermissionsResultCallback callback);

    boolean removeRequestPermissionsCallback(OnRequestPermissionsResultCallback callback);

    @RequiresApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, int requestCode);

}
