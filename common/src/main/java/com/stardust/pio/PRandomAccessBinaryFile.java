package com.stardust.pio;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/4/29.
 */

public class PRandomAccessBinaryFile extends RandomAccessFile {


    private RandomAccessFile mRandomAccessFile;

    public PRandomAccessBinaryFile(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    public PRandomAccessBinaryFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }

    public String readline() throws IOException {
        return super.readLine();
    }





}
