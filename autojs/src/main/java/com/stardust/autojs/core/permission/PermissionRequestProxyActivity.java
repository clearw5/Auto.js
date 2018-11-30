package com.stardust.autojs.core.permission;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public interface PermissionRequestProxyActivity {

    void addRequestPermissionsCallback(OnRequestPermissionsResultCallback callback);

    boolean removeRequestPermissionsCallback(OnRequestPermissionsResultCallback callback);

    @RequiresApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, int requestCode);

}
