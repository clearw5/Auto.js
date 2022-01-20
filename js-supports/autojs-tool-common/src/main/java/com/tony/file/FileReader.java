package com.tony.file;

import android.util.Log;

import com.stardust.autojs.runtime.ScriptRuntime;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    private ScriptRuntime runtime;
    private final String LOG_TAG = "FILE_READER";

    public FileReader(ScriptRuntime runtime) {
        this.runtime = runtime;
    }

    /**
     * 按行读取最后N行数据
     *
     * @param {string}   fileName 文件路径 支持相对路径
     * @param {number}   num 读取的行数
     * @param {number}   startReadIndex 偏移量
     * @param {function} filter (line) => check(line) 过滤内容
     * @returns { result: 行数据列表, readIndex: 当前读取偏移量, total: 文件总大小, filePath: 文件路径 }
     */
    public FileReadResult readLastLines(String fileName, long num, long startReadIndex, FileFilter filter) {
        if (filter == null) {
            filter = new NonFilter();
        }
        if (num <= 0) {
            num = 1000;
        }
        String filePath = runtime.files.path(fileName);
        if (!runtime.files.exists(filePath)) {
            runtime.console.error("文件不存在：" + filePath + " " + fileName);
            return null;
        }
        List<String> result = new ArrayList<>();
        try (RandomAccessFile rf = new RandomAccessFile(filePath, "r")) {
            long fileLength = rf.length();
            // 返回此文件中的当前偏移量
            long start = rf.getFilePointer();
            long readIndex = startReadIndex >= 0 ? startReadIndex : start + fileLength - 1;
            String line;
            rf.seek(readIndex);// 设置偏移量为文件末尾
            Log.d(LOG_TAG, "设置偏移量:" + readIndex + "开始位置:" + start);
            int c = -1;
            long lineCount = 0;
            while (readIndex > start) {
                c = rf.read();
                // console.verbose('read c', c)
                if (c == 10 || c == 13) {
                    line = rf.readLine();
                    // console.verbose('读取行', line)
                    if (line != null) {
                        line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        if (filter.match(line)) {
                            result.add(line);
                            lineCount++;
                        }
                    }
                    readIndex--;
                }
                if (lineCount >= num) {
                    break;
                }
                readIndex--;
                rf.seek(readIndex);
            }
            // 读取第一行
            if (readIndex == 0 && lineCount < num && lineCount > 0) {
                line = rf.readLine();
                if (line != null) {
                    line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    if (filter.match(line)) {
                        result.add(line);
                    }
                }
            }
            java.util.Collections.reverse(result);
            return new FileReadResult(result, readIndex, fileLength, filePath);
        } catch (Exception e) {
            runtime.console.error(e);
            return null;
        }
    }
    /**
     * 按行从前往后读取N行数据
     *
     * @param {string}   fileName 文件路径 支持相对路径
     * @param {number}   num 读取的行数
     * @param {number}   startReadIndex 开始行
     * @param {function} filter (line) => check(line) 过滤内容
     * @returns { result: 行数据列表, readIndex: 当前读取行, total: 文件总大小, filePath: 文件路径 }
     */
    public FileReadResult readForwardLines(String fileName, long num, long startReadIndex, FileFilter filter) {
        if (filter == null) {
            filter = new NonFilter();
        }
        if (num <= 0) {
            num = 1000;
        }
        String filePath = runtime.files.path(fileName);
        if (!runtime.files.exists(filePath)) {
            runtime.console.error("文件不存在：" + filePath + " " + fileName);
            return null;
        }
        List<String> result = new ArrayList<>();
        try (RandomAccessFile rf = new RandomAccessFile(filePath, "r")) {
            long fileLength = rf.length();
            // 返回此文件中的当前偏移量
            long start = rf.getFilePointer();
            long readIndex = startReadIndex < fileLength ? startReadIndex : 0;
            String line;
            // 设置偏移量为文件开头
            rf.seek(readIndex);
            Log.d(LOG_TAG, "设置偏移量:" + readIndex + "开始位置:" + start);
            int c = -1;
            long lineCount = 0;
            while (readIndex < fileLength) {
                line = rf.readLine();
                // console.verbose('读取行', line)
                if (line != null) {
                    byte[] readBytes = line.getBytes(StandardCharsets.ISO_8859_1);
                    // read length 当前行长度加上换行符
                    readIndex = rf.getFilePointer();
                    line = new String(readBytes, StandardCharsets.UTF_8);
                    if (filter.match(line)) {
                        result.add(line);
                        lineCount++;
                    }
                } else {
                    System.out.println("读取完毕 当前位置：" + readIndex + " 总大小：" + fileLength);
                    break;
                }
                if (lineCount >= num) {
                    break;
                }
            }
            return new FileReadResult(result, readIndex, fileLength, filePath);
        } catch (Exception e) {
            runtime.console.error(e);
            return null;
        }
    }

    public interface FileFilter {
        boolean match(String line);
    }

    public static class NonFilter implements FileFilter {

        @Override
        public boolean match(String line) {
            return true;
        }
    }

    public static class FileReadResult {
        private List<String> result;
        private long readIndex;
        private long total;
        private String filePath;

        public FileReadResult(List<String> result, long readIndex, long total, String filePath) {
            this.result = result;
            this.readIndex = readIndex;
            this.total = total;
            this.filePath = filePath;
        }

        public FileReadResult() {
        }

        public List<String> getResult() {
            return result;
        }

        public void setResult(List<String> result) {
            this.result = result;
        }

        public long getReadIndex() {
            return readIndex;
        }

        public void setReadIndex(long readIndex) {
            this.readIndex = readIndex;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
