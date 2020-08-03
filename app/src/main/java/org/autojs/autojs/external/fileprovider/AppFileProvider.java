package org.autojs.autojs.external.fileprovider;

import android.content.Context;
import android.net.Uri;

import org.autojs.autojs.BuildConfig;

import androidx.core.content.FileProvider;

import java.io.File;

public class AppFileProvider extends FileProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, AUTHORITY, file);
    }
}
