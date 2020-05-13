package com.tony.util;

import com.tony.listener.DefaultDownloaderListener;
import com.tony.listener.DownloaderListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author TonyJiang 2019/11/21
 */
public class ZipUtil {

    private List<String> zipIgnoredFiles;
    private List<String> unzipSkipFiles;
    private DownloaderListener listener;
    private List<String> compressedFiles;
    private String rootPath;
    private String dstPath;

    public ZipUtil() {
        this.zipIgnoredFiles = new ArrayList<>();
        this.unzipSkipFiles = new ArrayList<>();
        this.compressedFiles = new ArrayList<>();
    }


    public boolean compress(String srcPath, String dstPath) {
        rootPath = srcPath;
        this.dstPath = dstPath;
        this.compressedFiles = new ArrayList<>();
        File srcFile = new File(srcPath);
        File dstFile = new File(dstPath);
        if (!srcFile.exists()) {
            getListener().updateGui("原路径文件不存在：" + srcPath);
            return false;
        }
        try (
                FileOutputStream fOut = new FileOutputStream(dstFile);
                CheckedOutputStream cos = new CheckedOutputStream(fOut, new CRC32());
                ZipOutputStream zipOut = new ZipOutputStream(cos)
        ) {
            compress(srcFile, zipOut, "");
        } catch (Exception e) {
            getListener().updateGui("压缩日常" + e.getMessage());
        }
        return checkCompressedStatus();
    }

    private void compress(File file, ZipOutputStream zipOut, String baseDir) {
        if (file.isDirectory()) {
            if (zipIgnoredFiles.contains(getRelativeFileName(file))) {
                getListener().updateGui("跳过文件夹：" + file.getPath());
                return;
            }
            compressDirectory(file, zipOut, baseDir);
        } else {
            compressFile(file, zipOut, baseDir);
        }
    }

    private String getRelativeFileName(File file) {
        String relativeFilePath = file.getPath().substring(rootPath.length());
        System.out.println("待压缩文件相对位置：" + relativeFilePath);
        return relativeFilePath;
    }

    private boolean checkCompressedStatus() {
        if (compressedFiles != null && compressedFiles.size() > 0) {
            getListener().updateGui("备份文件成功：" + dstPath);
            return true;
        }
        // 删除创建的 空文件
        File dstFile = new File(dstPath);
        if (dstFile.exists()) {
            getListener().updateGui("删除创建的空压缩文件：" + dstFile + " " + dstFile.delete());
        }
        return false;
    }

    /**
     * 压缩一个目录
     */
    private void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                compress(file, zipOut, baseDir + dir.getName() + "/");
            }
        }
    }

    /**
     * 压缩一个文件
     */
    private void compressFile(File file, ZipOutputStream zipOut, String baseDir) {
        if (!file.exists()) {
            return;
        }
        String relativeName = getRelativeFileName(file);
        if (zipIgnoredFiles.indexOf(relativeName) > 0) {
            getListener().updateGui("跳过文件：" + relativeName);
            return;
        }
        try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))
        ) {
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zipOut.putNextEntry(entry);
            int count;
            int buffer = 8192;
            byte[] data = new byte[buffer];
            while ((count = bis.read(data, 0, buffer)) > 0) {
                zipOut.write(data, 0, count);
            }
            compressedFiles.add(relativeName);
        } catch (IOException e) {
            getListener().updateGui("文件读取异常 " + file.getName() + " " + e.getMessage());
        }
    }

    public boolean decompress(File file, String outputDir) {
        if (file == null) {
            return false;
        }
        getListener().updateGui("开始解压：");
        String rootDirName = null;
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry = null;
            ZipFile zipFile = new ZipFile(file);

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                if (rootDirName == null) {
                    rootDirName = fileName;
                    getListener().updateGui("跳过主目录文件：" + rootDirName);
                    continue;
                } else {
                    fileName = fileName.substring(rootDirName.length());
                }
                if (unzipSkipFiles != null && unzipSkipFiles.size() > 0 && unzipSkipFiles.contains(fileName)) {
                    getListener().updateGui("跳过文件：" + fileName);
                    continue;
                }
                getListener().updateGui("解压文件：" + fileName);
                File temp = new File(outputDir + "/" + fileName);
                boolean dirExists = true;
                if (!temp.getParentFile().exists()) {
                    getListener().updateGui("创建文件夹：" + temp.getParent());
                    dirExists = temp.getParentFile().mkdirs();
                }
                if (zipEntry.isDirectory()) {
                    getListener().updateGui(temp.isDirectory() + " 跳过文件夹：" + fileName);
                    continue;
                }
                if (!dirExists) {
                    getListener().updateGui("创建父文件夹失败" + fileName);
                    continue;
                }

                try (
                        // 文件缓冲区
                        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(temp));
                        // 通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
                        InputStream is = zipFile.getInputStream(zipEntry);
                ) {
                    byte[] buff = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buff)) != -1) {
                        os.write(buff, 0, len);
                    }
                } catch (IOException e) {
                    getListener().updateGui("解压文件" + fileName + "异常" + e.getMessage());
                }
            }
        } catch (IOException e) {
            getListener().updateGui("读取压缩文件异常：" + e.getMessage());
            return false;
        }
        getListener().updateGui("解压完成！");
        return true;
    }

    public void setZipIgnoredFiles(List<String> zipIgnoredFiles) {
        for (String uncheckedFile : zipIgnoredFiles) {
            if (uncheckedFile.startsWith("/")) {
                if (uncheckedFile.length() > 1) {
                    this.zipIgnoredFiles.add(uncheckedFile);
                }
            } else {
                if (uncheckedFile.length() > 0) {
                    this.zipIgnoredFiles.add("/" + uncheckedFile);
                }
            }
        }
    }

    public void setUnzipSkipFiles(List<String> unzipSkipFiles) {
        this.unzipSkipFiles = unzipSkipFiles;
    }

    public void setListener(DownloaderListener listener) {
        this.listener = listener;
    }

    private DownloaderListener getListener() {
        if (listener == null) {
            listener = new DefaultDownloaderListener();
        }
        return listener;
    }
}
