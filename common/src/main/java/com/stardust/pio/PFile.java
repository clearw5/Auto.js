package com.stardust.pio;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by Stardust on 2017/4/1.
 */

public class PFile {

    private static final String TAG = "PFile";

    static final int DEFAULT_BUFFER_SIZE = 8192;
    static final String DEFAULT_ENCODING = Charset.defaultCharset().name();

    public static PFile open(String path, String mode, String encoding, int bufferSize) {
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

    public static PFile open(String path, String mode, String encoding) {
        return open(path, mode, encoding, DEFAULT_BUFFER_SIZE);
    }

    public static PFile open(String path, String mode) {
        return open(path, mode, DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }

    public static PFile open(String path) {
        return open(path, "r", DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }


    public static boolean createIfNotExists(String path) {
        ensureDirectory(path);
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

    public static boolean ensureDirectory(String path) {
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
            e.printStackTrace();
            throw new RuntimeException(e);
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String read(InputStream inputStream) {
        return read(inputStream, "utf-8");
    }

    public static boolean copyRaw(Context context, int rawId, String path) {
        InputStream is = context.getResources().openRawResource(rawId);
        return copyStream(is, path);
    }

    public static boolean copyStream(InputStream is, String path) {
        if (!ensureDirectory(path))
            return false;
        File file = new File(path);
        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return false;
            FileOutputStream fos = new FileOutputStream(file);
            return write(is, fos);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean write(InputStream is, OutputStream os) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        try {
            while (is.available() > 0) {
                int n = is.read(buffer);
                os.write(buffer, 0, n);
            }
            is.close();
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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

    public static String renameWithoutExtension(String path, String newName) {
        File file = new File(path);
        File newFile = new File(file.getParent(), newName + getExtension(file.getName()));
        file.renameTo(newFile);
        return newFile.getAbsolutePath();
    }

    public static String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0 || i == fileName.length() - 1)
            return "";
        return fileName.substring(i);
    }

    public static boolean write(String path, String text) {
        return write(new File(path), text);
    }

    public static boolean write(File file, String text) {
        try {
            return write(new FileOutputStream(file), text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean write(OutputStream outputStream, String text) {
        try {
            outputStream.write(text.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    public static String getNameWithoutExtension(String fileName) {
        int a = fileName.lastIndexOf('/');
        if (a < 0)
            a = fileName.lastIndexOf('\\');
        if (a < 0)
            a = -1;
        int b = fileName.indexOf('.', a + 1);
        if (b < 0)
            b = fileName.length();
        fileName = fileName.substring(a + 1, b);
        return fileName;
    }

    public static File copyAssetToTmpFile(Context context, String path) {
        String extension = getExtension(path);
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
        for (File child : file.listFiles()) {
            if (!deleteRecursively(child))
                return false;
        }
        return file.delete();
    }

    public static String readAsset(AssetManager assets, String path) {
        try {
            return read(assets.open(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
