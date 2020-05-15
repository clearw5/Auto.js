package com.stardust.autojs.rhino;

import android.util.Log;

import java.io.File;

public class DeleteOnFinalizeFile {

    private File fileObject;

    private String LOG_TAG = "DeleteOnFinalizeFile";

    public DeleteOnFinalizeFile(File file) {
        fileObject = file;
    }

    @Override
    protected void finalize() throws Throwable {
        if (fileObject != null) {
            Log.d(LOG_TAG, "finalize delete file" + fileObject.getName());
            if (fileObject.exists())
                fileObject.delete();
        }
        super.finalize();
    }
}
