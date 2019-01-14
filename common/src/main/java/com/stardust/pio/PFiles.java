package com.stardust.pio;

import android.app.NativeActivity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.stardust.util.Func1;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PFiles {

    private static final String TAG = "PFiles";

    static final int DEFAULT_BUFFER_SIZE = 8192;
    static final String DEFAULT_ENCODING = Charset.defaultCharset().name();

    public static PFileInterface open(String path, String mode, String encoding, int bufferSize) {
        switch (mode) {
            case "r":
                return new PReadableTextFile(path, encoding, bufferSize);
            case "w":
                return new PWritableTextFile(path, encoding, bufferSize, false);
            case "a":
                return new PWritableTextFile(path, encoding, bufferSize, true);
        }
        return null;
    }

    public static Object open(String path, String mode, String encoding) {
        return open(path, mode, encoding, DEFAULT_BUFFER_SIZE);
    }

    public static Object open(String path, String mode) {
        return open(path, mode, DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }

    public static Object open(String path) {
        return open(path, "r", DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }

    public static boolean create(String path) {
        File f = new File(path);
        if (path.endsWith(File.separator)) {
            return f.mkdir();
        } else {
            try {
                return f.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
    }

    public static boolean createIfNotExists(String path) {
        ensureDir(path);
        File file = new File(path);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean createWithDirs(String path) {
        return createIfNotExists(path);
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean ensureDir(String path) {
        int i = path.lastIndexOf("\\");
        if (i < 0)
            i = path.lastIndexOf("/");
        if (i >= 0) {
            String folder = path.substring(0, i);
            File file = new File(folder);
            if (file.exists())
                return true;
            return file.mkdirs();
        } else {
            return false;
        }
    }

    public static String read(String path, String encoding) {
        return read(new File(path), encoding);
    }

    public static String read(String path) {
        return read(new File(path));
    }


    public static String read(File file, String encoding) {
        try {
            return read(new FileInputStream(file), encoding);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String read(File file) {
        return read(file, "utf-8");
    }

    public static String read(InputStream is, String encoding) {
        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            closeSilently(is);
        }
    }

    public static String read(InputStream inputStream) {
        return read(inputStream, "utf-8");
    }

    public static byte[] readBytes(InputStream is) {
        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return bytes;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean copyRaw(Context context, int rawId, String path) {
        InputStream is = context.getResources().openRawResource(rawId);
        return copyStream(is, path);
    }

    public static boolean copyStream(InputStream is, String path) {
        if (!ensureDir(path))
            return false;
        File file = new File(path);
        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return false;
            FileOutputStream fos = new FileOutputStream(file);
            write(is, fos);
            return true;
        } catch (IOException | UncheckedIOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void write(InputStream is, OutputStream os, boolean close) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        try {
            while (is.available() > 0) {
                int n = is.read(buffer);
                if (n > 0) {
                    os.write(buffer, 0, n);
                }
            }
            if (close) {
                is.close();
                os.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(InputStream is, OutputStream os) {
        write(is, os, true);
    }


    public static void write(String path, String text) {
        write(new File(path), text);
    }

    public static void write(String path, String text, String encoding) {
        try {
            write(new FileOutputStream(path), text, encoding);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(File file, String text) {
        try {
            write(new FileOutputStream(file), text);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(OutputStream fileOutputStream, String text) {
        write(fileOutputStream, text, "utf-8");
    }


    public static void write(OutputStream outputStream, String text, String encoding) {
        try {
            outputStream.write(text.getBytes(encoding));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            closeSilently(outputStream);
        }
    }

    public static void append(String path, String text) {
        create(path);
        try {
            write(new FileOutputStream(path, true), text);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static void append(String path, String text, String encoding) {
        create(path);
        try {
            write(new FileOutputStream(path, true), text, encoding);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeBytes(OutputStream outputStream, byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void appendBytes(String path, byte[] bytes) {
        create(path);
        try {
            writeBytes(new FileOutputStream(path, true), bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeBytes(String path, byte[] bytes) {
        try {
            writeBytes(new FileOutputStream(path), bytes);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean copy(String pathFrom, String pathTo) {
        try {
            return copyStream(new FileInputStream(pathFrom), pathTo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAsset(Context context, String assetFile, String path) {
        try {
            return copyStream(context.getAssets().open(assetFile), path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void copyAssetDir(AssetManager manager, String assetsDir, String toDir, String[] list) throws IOException {
        new File(toDir).mkdirs();
        if (list == null) {
            list = manager.list(assetsDir);
        }
        if (list == null)
            throw new IOException("not a directory: " + assetsDir);
        for (String file : list) {
            if (TextUtils.isEmpty(file)) {
                continue;
            }
            String fullAssetsPath = join(assetsDir, file);
            String[] children = manager.list(fullAssetsPath);
            if (children == null || children.length == 0) {
                InputStream stream = null;
                try {
                    stream = manager.open(fullAssetsPath);
                    copyStream(stream, join(toDir, file));
                } catch (IOException e) {
                    throw e;
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } else {
                copyAssetDir(manager, fullAssetsPath, join(toDir, file), children);
            }
        }
    }

    public static String renameWithoutExtensionAndReturnNewPath(String path, String newName) {
        File file = new File(path);
        File newFile = new File(file.getParent(), newName + "." + getExtension(file.getName()));
        file.renameTo(newFile);
        return newFile.getAbsolutePath();
    }

    public static boolean renameWithoutExtension(String path, String newName) {
        File file = new File(path);
        File newFile = new File(file.getParent(), newName + "." + getExtension(file.getName()));
        return file.renameTo(newFile);
    }

    public static boolean rename(String path, String newName) {
        File f = new File(path);
        return f.renameTo(new File(f.getParent(), newName));
    }

    public static boolean move(String path, String newPath) {
        File f = new File(path);
        return f.renameTo(new File(newPath));
    }

    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0 || i + 1 >= fileName.length() - 1)
            return "";
        return fileName.substring(i + 1);
    }

    public static String generateNotExistingPath(String path, String extension) {
        if (!new File(path + extension).exists())
            return path + extension;
        int i = 0;
        while (true) {
            String pathI = path + "(" + i + ")" + extension;
            if (!new File(pathI).exists())
                return pathI;
            i++;
        }
    }

    public static String getName(String filePath) {
        filePath = filePath.replace('\\', '/');
        return new File(filePath).getName();
    }

    public static String getNameWithoutExtension(String filePath) {
        String fileName = getName(filePath);
        int b = fileName.lastIndexOf('.');
        if (b < 0)
            b = fileName.length();
        fileName = fileName.substring(0, b);
        return fileName;
    }

    public static File copyAssetToTmpFile(Context context, String path) {
        String extension = "." + getExtension(path);
        String name = getNameWithoutExtension(path);
        if (name.length() < 5) {
            name += name.hashCode();
        }
        try {
            File tmpFile = File.createTempFile(name, extension, context.getCacheDir());
            copyAsset(context, path, tmpFile.getPath());
            return tmpFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteRecursively(File file) {
        if (file.isFile())
            return file.delete();
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (!deleteRecursively(child))
                    return false;
            }
        }
        return file.delete();
    }

    public static boolean deleteFilesOfDir(File dir) {
        if (!dir.isDirectory())
            throw new IllegalArgumentException("not a directory: " + dir);
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (!deleteRecursively(child))
                    return false;
            }
        }
        return true;
    }

    public static boolean remove(String path) {
        return new File(path).delete();
    }

    public static boolean removeDir(String path) {
        return deleteRecursively(new File(path));
    }

    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String readAsset(AssetManager assets, String path) {
        try {
            return read(assets.open(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String[] listDir(String path) {
        File file = new File(path);
        return wrapNonNull(file.list());
    }

    private static String[] wrapNonNull(String[] list) {
        if (list == null)
            return new String[0];
        return list;
    }

    public static String[] listDir(String path, final Func1<String, Boolean> filter) {
        final File file = new File(path);
        return wrapNonNull(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return filter.call(name);
            }
        }));
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static boolean isDir(String path) {
        return new File(path).isDirectory();
    }

    public static boolean isEmptyDir(String path) {
        File file = new File(path);
        return file.isDirectory() && file.list().length == 0;
    }

    public static String join(String base, String... paths) {
        File file = new File(base);
        for (String path : paths) {
            file = new File(file, path);
        }
        return file.getPath();
    }

    public static String getHumanReadableSize(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".substring(exp - 1, exp);
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getSimplifiedPath(String path) {
        if (path.startsWith(Environment.getExternalStorageDirectory().getPath())) {
            return path.substring(Environment.getExternalStorageDirectory().getPath().length());
        }
        return path;
    }

    public static byte[] readBytes(String path) {
        try {
            return readBytes(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {

        }
    }
}
