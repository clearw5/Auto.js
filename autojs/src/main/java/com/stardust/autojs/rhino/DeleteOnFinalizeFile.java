package com.stardust.autojs.rhino;

import org.apache.log4j.Logger;

import java.io.File;

public class DeleteOnFinalizeFile {

    private File fileObject;

    private static final Logger logger = Logger.getLogger("DeleteOnFinalizeFile");

    public DeleteOnFinalizeFile(File file) {
        fileObject = file;
    }
/*
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (fileObject != null) {
            logger.debug("finalize delete file " + fileObject.getName());
            if (fileObject.exists())
                fileObject.delete();
        }
    }
 */
}
