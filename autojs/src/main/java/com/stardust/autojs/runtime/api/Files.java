package com.stardust.autojs.runtime.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.pio.PFileInterface;
import com.stardust.pio.PFiles;
import com.stardust.util.Func1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Stardust on 2018/1/23.
 */

public class Files {

    private final ScriptRuntime mRuntime;

    public Files(ScriptRuntime runtime) {
        mRuntime = runtime;
    }

    public String cwd() {
        return ((ScriptEngine.AbstractScriptEngine) mRuntime.engines.myEngine()).cwd();
    }

    public static PFileInterface open(String path, String mode, String encoding, int bufferSize) {
        return PFiles.open(path, mode, encoding, bufferSize);
    }

    public static Object open(String path, String mode, String encoding) {
        return PFiles.open(path, mode, encoding);
    }

    public static Object open(String path, String mode) {
        return PFiles.open(path, mode);
    }

    public static Object open(String path) {
        return PFiles.open(path);
    }

    public static boolean create(String path) {
        return PFiles.create(path);
    }

    public static boolean createIfNotExists(String path) {
        return PFiles.createIfNotExists(path);
    }

    public static boolean createWithDirs(String path) {
        return PFiles.createWithDirs(path);
    }

    public static boolean exists(String path) {
        return PFiles.exists(path);
    }

    public static boolean ensureDir(String path) {
        return PFiles.ensureDir(path);
    }

    public static String read(String path, String encoding) {
        return PFiles.read(path, encoding);
    }

    public static String read(String path) {
        return PFiles.read(path);
    }

    public static String read(File file, String encoding) {
        return PFiles.read(file, encoding);
    }

    public static String read(File file) {
        return PFiles.read(file);
    }

    public static String read(InputStream is, String encoding) {
        return PFiles.read(is, encoding);
    }

    public static String read(InputStream inputStream) {
        return PFiles.read(inputStream);
    }

    public static byte[] readBytes(InputStream is) {
        return PFiles.readBytes(is);
    }

    public static boolean copyRaw(Context context, int rawId, String path) {
        return PFiles.copyRaw(context, rawId, path);
    }

    public static boolean copyStream(InputStream is, String path) {
        return PFiles.copyStream(is, path);
    }

    public static void write(InputStream is, OutputStream os) {
        PFiles.write(is, os);
    }

    public static void write(String path, String text) {
        PFiles.write(path, text);
    }

    public static void write(String path, String text, String encoding) {
        PFiles.write(path, text, encoding);
    }

    public static void write(File file, String text) {
        PFiles.write(file, text);
    }

    public static void write(FileOutputStream fileOutputStream, String text) {
        PFiles.write(fileOutputStream, text);
    }

    public static void write(OutputStream outputStream, String text, String encoding) {
        PFiles.write(outputStream, text, encoding);
    }

    public static void append(String path, String text) {
        PFiles.append(path, text);
    }

    public static void append(String path, String text, String encoding) {
        PFiles.append(path, text, encoding);
    }

    public static void writeBytes(OutputStream outputStream, byte[] bytes) {
        PFiles.writeBytes(outputStream, bytes);
    }

    public static void appendBytes(String path, byte[] bytes) {
        PFiles.appendBytes(path, bytes);
    }

    public static void writeBytes(String path, byte[] bytes) {
        PFiles.writeBytes(path, bytes);
    }

    public static boolean copy(String pathFrom, String pathTo) {
        return PFiles.copy(pathFrom, pathTo);
    }

    public static boolean copyAsset(Context context, String assetFile, String path) {
        return PFiles.copyAsset(context, assetFile, path);
    }

    public static String renameWithoutExtensionAndReturnNewPath(String path, String newName) {
        return PFiles.renameWithoutExtensionAndReturnNewPath(path, newName);
    }

    public static boolean renameWithoutExtension(String path, String newName) {
        return PFiles.renameWithoutExtension(path, newName);
    }

    public static boolean rename(String path, String newName) {
        return PFiles.rename(path, newName);
    }

    public static boolean move(String path, String newPath) {
        return PFiles.move(path, newPath);
    }

    public static String getExtension(String fileName) {
        return PFiles.getExtension(fileName);
    }

    public static String generateNotExistingPath(String path, String extension) {
        return PFiles.generateNotExistingPath(path, extension);
    }

    public static String getName(String filePath) {
        return PFiles.getName(filePath);
    }

    public static String getNameWithoutExtension(String filePath) {
        return PFiles.getNameWithoutExtension(filePath);
    }

    public static File copyAssetToTmpFile(Context context, String path) {
        return PFiles.copyAssetToTmpFile(context, path);
    }

    public static boolean deleteRecursively(File file) {
        return PFiles.deleteRecursively(file);
    }

    public static boolean remove(String path) {
        return PFiles.remove(path);
    }

    public static boolean removeDir(String path) {
        return PFiles.removeDir(path);
    }

    public static String getSdcardPath() {
        return PFiles.getSdcardPath();
    }

    public static String readAsset(AssetManager assets, String path) {
        return PFiles.readAsset(assets, path);
    }

    public static String[] listDir(String path) {
        return PFiles.listDir(path);
    }

    public static String[] listDir(String path, Func1<String, Boolean> filter) {
        return PFiles.listDir(path, filter);
    }

    public static boolean isFile(String path) {
        return PFiles.isFile(path);
    }

    public static boolean isDir(String path) {
        return PFiles.isDir(path);
    }

    public static boolean isEmptyDir(String path) {
        return PFiles.isEmptyDir(path);
    }

    public static String join(String parent, String... child) {
        return PFiles.join(parent, child);
    }

    public static String getHumanReadableSize(long bytes) {
        return PFiles.getHumanReadableSize(bytes);
    }

    public static String getSimplifiedPath(String path) {
        return PFiles.getSimplifiedPath(path);
    }
}
