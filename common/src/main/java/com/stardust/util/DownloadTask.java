package com.stardust.util;

import android.os.AsyncTask;

import com.stardust.net.AutoHttpURLConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by Stardust on 2017/4/10.
 */

public class DownloadTask extends AsyncTask<String, Integer, Boolean> {

    public interface ProgressListener {
        void publishProgress(int i);
    }

    public static class Download implements Callable<Boolean> {

        private String mUrl;
        private String mPath;
        private ProgressListener mListener;
        private volatile boolean mCanceled = false;

        public Download(String url, String path, ProgressListener listener) {
            mUrl = url;
            mPath = path;
            mListener = listener;
        }

        @Override
        public Boolean call() throws IOException {
            try (AutoHttpURLConnection connection = new AutoHttpURLConnection(mUrl)) {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
                int total = connection.getContentLength();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(mPath);
                return download(input, output, total);
            } catch (Exception e) {
                throw e;
            }
        }

        public void cancel() {
            mCanceled = true;
        }

        private boolean download(InputStream input, FileOutputStream output, int total) throws IOException {
            byte buffer[] = new byte[8192];
            long downloaded = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                if (mCanceled) {
                    input.close();
                    return false;
                }
                downloaded += read;
                if (total > 0)
                    publishProgress((int) (downloaded * 100 / total));
                output.write(buffer, 0, read);
            }
            return true;
        }

        private void publishProgress(int i) {
            if (mListener != null) {
                mListener.publishProgress(i);
            }
        }
    }

    private Download mDownload;

    @Override
    protected Boolean doInBackground(String... params) {
        mDownload = new Download(params[0], params[1], new ProgressListener() {
            @Override
            public void publishProgress(int i) {
                DownloadTask.this.publishProgress(i);
            }
        });
        try {
            return mDownload.call();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mDownload = null;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (mDownload != null)
            mDownload.cancel();
    }
}
