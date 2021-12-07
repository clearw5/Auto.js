package com.tony.autojs.common;


import android.util.Log;

import com.stardust.autojs.runtime.ScriptRuntime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 基于文件的单向通信
 */
public class ProcessMappedShare {
    private int bufferSize;
    private final boolean isSubscriber;
    private final RandomAccessFile randomAccessFile;
    private boolean loop;
    private long timeout;
    private long interval;
    private volatile boolean subscribing;
    private volatile boolean unsubscribe;
    private Object threads;

    /**
     * 指定文件路径、通信数据缓冲区字节大小
     *
     * @param targetFilePath
     * @param bufferSize
     */
    private ProcessMappedShare(String targetFilePath, int bufferSize, boolean isSubscriber, ScriptRuntime scriptRuntime) throws IOException {
        File targetFile = new File(targetFilePath);
        File parentFile = targetFile.getParentFile();
        if (parentFile == null || !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IllegalArgumentException("创建映射文件失败，请确认路径是否合法！path: " + targetFilePath);
        }
        if (targetFile.isDirectory()) {
            throw new IllegalArgumentException("指定文件为路径！");
        }
        randomAccessFile = new RandomAccessFile(targetFile, "rw");
        this.bufferSize = bufferSize;
        if (this.bufferSize <= 0) {
            this.bufferSize = 1024;
        }
        this.isSubscriber = isSubscriber;
        timeout = 60;
        interval = 1000;
        threads = scriptRuntime.threads;
    }

    public static ProcessMappedShare newSubscriber(String targetFilePath, int bufferSize, ScriptRuntime scriptRuntime) throws IOException {
        return new ProcessMappedShare(targetFilePath, bufferSize, true, scriptRuntime);
    }

    public static ProcessMappedShare newProvider(String targetFilePath, int bufferSize, ScriptRuntime scriptRuntime) throws IOException {
        return new ProcessMappedShare(targetFilePath, bufferSize, false, scriptRuntime);
    }

    /**
     * 订阅消息 读取信息完毕后触发回调
     *
     * @param callback
     * @throws IOException
     */
    public void subscribe(final Callback callback) throws Exception {
        if (!isSubscriber) {
            throw new IllegalStateException("当前不是订阅模式，无法订阅消息");
        }
        if (subscribing) {
            throw new IllegalStateException("当前正在订阅中，请勿重复订阅");
        }
        FileChannel fc = randomAccessFile.getChannel();
        final MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
        final byte terminate = '\n';
        clearBuffer(mbb);
        unsubscribe = false;
        subscribing = true;
        final Thread runningThread = start(new Runnable() {
            @Override
            public void run() {
                do {
                    byte flag = mbb.get(0);
                    while (flag != (byte) 2) {
                        flag = mbb.get(0);
                        // 超时自动终止
                        if (Thread.currentThread().isInterrupted() || unsubscribe) {
                            subscribing = false;
                            return;
                        }
                        try {
                            synchronized (this) {
                                wait(interval);
                            }
                        } catch (InterruptedException e) {
                            // e.printStackTrace();
                        }
                    }
                    int index = 1;
                    byte read = mbb.get(index++);
                    do {
                        bos.write(read);
                        read = mbb.get(index++);
                    } while (read != terminate);

                    String result = new String(bos.toByteArray(), StandardCharsets.UTF_8);
                    callback.call(result);
                    bos.reset();
                    clearBuffer(mbb);
                } while (loop && !Thread.currentThread().isInterrupted() && !unsubscribe);
                subscribing = false;
            }
        });

        if (!this.loop) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    while (count++ < timeout && subscribing) {
                        try {
                            synchronized (this) {
                                this.wait(1000);
                            }
                        } catch (Exception e) {
                            //
                        }
                    }
                    if (runningThread.isAlive()) {
                        Log.d("PROCESS_MAPPED_SHARE", "订阅超时");
                        callback.call("订阅超时");
                        runningThread.interrupt();
                    }
                }
            }).start();
        }
    }

    /**
     * 取消订阅
     */
    public void unsubscribe() {
        this.unsubscribe = true;
    }

    /**
     * 清空文件内容
     *
     * @param mbb
     */
    private void clearBuffer(MappedByteBuffer mbb) {
        // 清除文件内容
        for (int i = 0; i < bufferSize; i++) {
            mbb.put(i, (byte) 10);
        }
    }

    /**
     * 设置是否循环读取
     *
     * @param loop
     * @return
     */
    public ProcessMappedShare setLoop(boolean loop) {
        if (!isSubscriber) {
            throw new IllegalStateException("只有订阅模式可以设置当前配置");
        }
        this.loop = loop;
        return this;
    }

    /**
     * 设置文件监听的间隔时间 默认1000ms
     *
     * @param interval
     * @return
     */
    public ProcessMappedShare setInterval(long interval) {
        if (!isSubscriber) {
            throw new IllegalStateException("只有订阅模式可以设置当前配置");
        }
        if (interval <= 0) {
            interval = 1000;
        }
        this.interval = interval;
        return this;
    }

    /**
     * 设置最长订阅时间 循环模式无限制时长
     *
     * @param timeout
     * @return
     */
    public ProcessMappedShare timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 发送消息 将内容写入文件
     *
     * @param data
     * @throws IOException
     */
    public void postInfo(String data) throws IOException {
        if (isSubscriber) {
            throw new IllegalStateException("当前是订阅模式，无法发送消息");
        }
        if (data == null || data.length() <= 0) {
            throw new IllegalArgumentException("不能发送空内容");
        }
        FileChannel fc = randomAccessFile.getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);

        byte[] bytes = data.getBytes();
        int length = bytes.length;
        if (length > bufferSize) {
            throw new IllegalArgumentException(String.format("发送字节大小[%d]大于缓冲区最大大小[%d]", length, bufferSize));
        }
        byte marker = mbb.get(0);
        if (marker == 1) {
            throw new IllegalStateException("文件正在被写入，无法发送消息");
        }
        mbb.put(0, (byte) 1);
        int index = 1;
        for (byte b : bytes) {
            mbb.put(index++, b);
        }
        // 标记可读取
        mbb.put(0, (byte) 2);
    }

    /**
     * 兼容Pro版 通过反射获取Threads的start方法
     *
     * @param runnable
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Thread start(Runnable runnable) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (threads == null) {
            throw new IllegalStateException("threads is null 无法创建线程");
        }
        Method method = threads.getClass().getMethod("start", Runnable.class);
        return (Thread) method.invoke(threads, runnable);
    }

    public interface Callback {
        void call(String value);
    }
}
