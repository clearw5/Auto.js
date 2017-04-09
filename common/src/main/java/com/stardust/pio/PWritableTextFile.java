package com.stardust.pio;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PWritableTextFile extends PFile {

    public static PWritableTextFile open(String path, String encoding, int bufferSize) {
        return new PWritableTextFile(path, encoding, bufferSize, false);
    }

    public static PWritableTextFile open(String path, String encoding) {
        return new PWritableTextFile(path, encoding);
    }

    public static PWritableTextFile open(String path, boolean append) {
        return new PWritableTextFile(path, append);
    }

    public static PWritableTextFile open(String path) {
        return new PWritableTextFile(path);
    }

    private BufferedWriter mBufferedWriter;

    public PWritableTextFile(String path, String encoding, int bufferingSize, boolean append) {
        if (bufferingSize <= 0) {
            bufferingSize = PFile.DEFAULT_BUFFER_SIZE;
        }
        try {
            mBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), encoding), bufferingSize);
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public PWritableTextFile(String path) {
        this(path, PFile.DEFAULT_ENCODING, PFile.DEFAULT_BUFFER_SIZE, false);
    }

    public PWritableTextFile(String path, boolean append) {
        this(path, PFile.DEFAULT_ENCODING, PFile.DEFAULT_BUFFER_SIZE, append);
    }

    public PWritableTextFile(String path, int bufferSize) {
        this(path, PFile.DEFAULT_ENCODING, bufferSize, false);
    }

    public PWritableTextFile(String path, String encoding) {
        this(path, encoding, DEFAULT_BUFFER_SIZE, false);
    }

    public PWritableTextFile(String path, String encoding, boolean append) {
        this(path, encoding, DEFAULT_BUFFER_SIZE, append);
    }

    public PWritableTextFile(String path, String encoding, int bufferSize) {
        this(path, encoding, bufferSize, false);
    }


    public void write(String str) {
        try {
            mBufferedWriter.write(str);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeline(String line) {
        try {
            mBufferedWriter.write(line);
            mBufferedWriter.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writelines(String[] lines) {
        writelines(Arrays.asList(lines));
    }

    public void writelines(List<String> lines) {
        for (String line : lines) {
            writeline(line);
        }
    }

    public void close() {
        try {
            mBufferedWriter.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void flush() {
        try {
            mBufferedWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
