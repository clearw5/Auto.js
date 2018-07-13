package org.autojs.autojs.network.download;

import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stardust.concurrent.VolatileBox;
import com.stardust.pio.PFiles;

import org.autojs.autojs.R;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.api.DownloadApi;
import org.autojs.autojs.tool.SimpleObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Stardust on 2017/10/20.
 */

public class DownloadManager {

    private static final String LOG_TAG = "DownloadManager";
    private static DownloadManager sInstance;

    private static final int RETRY_COUNT = 3;
    private Retrofit mRetrofit;
    private DownloadApi mDownloadApi;
    private ConcurrentHashMap<String, VolatileBox<Boolean>> mDownloadStatuses = new ConcurrentHashMap<>();

    public DownloadManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NodeBB.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            int tryCount = 0;
                            while (!response.isSuccessful() && tryCount < RETRY_COUNT) {
                                tryCount++;
                                response = chain.proceed(request);
                            }
                            return response;
                        })
                        .build()
                )
                .build();
        mDownloadApi = mRetrofit.create(DownloadApi.class);
    }


    public static DownloadManager getInstance() {
        if (sInstance == null) {
            sInstance = new DownloadManager();
        }
        return sInstance;
    }


    public static String parseFileNameLocally(String url) {
        int i = url.lastIndexOf('-');
        if (i < 0) {
            i = url.lastIndexOf('/');
        }
        return URLDecoder.decode(url.substring(i + 1));
    }

    public Observable<Integer> download(String url, String path) {
        DownloadTask task = new DownloadTask(url, path);
        mDownloadApi.download(url)
                .subscribeOn(Schedulers.io())
                .subscribe(task::start, error -> task.progress().onError(error));
        return task.progress();
    }

    public Observable<File> downloadWithProgress(Context context, String url, String path) {
        String fileName = DownloadManager.parseFileNameLocally(url);
        return download(url, path, createDownloadProgressDialog(context, url, fileName));
    }

    private MaterialDialog createDownloadProgressDialog(Context context, String url, String fileName) {
        return new MaterialDialog.Builder(context)
                .progress(false, 100)
                .title(fileName)
                .cancelable(false)
                .positiveText(R.string.text_cancel_download)
                .onPositive((dialog, which) -> DownloadManager.getInstance().cancelDownload(url))
                .show();
    }

    private Observable<File> download(String url, String path, MaterialDialog progressDialog) {
        PublishSubject<File> subject = PublishSubject.create();
        DownloadManager.getInstance().download(url, path)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(progressDialog::setProgress)
                .subscribe(new SimpleObserver<Integer>() {
                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                        subject.onNext(new File(path));
                        subject.onComplete();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(LOG_TAG, "Download failed", error);
                        progressDialog.dismiss();
                        subject.onError(error);
                    }
                });
        return subject;
    }

    public void cancelDownload(String url) {
        VolatileBox<Boolean> status = mDownloadStatuses.get(url);
        if (status != null) {
            status.set(false);
        }
    }

    private class DownloadTask {

        private String mUrl;
        private String mPath;
        private VolatileBox<Boolean> mStatus;
        private InputStream mInputStream;
        private FileOutputStream mFileOutputStream;
        private PublishSubject<Integer> mProgress;

        public DownloadTask(String url, String path) {
            mUrl = url;
            mPath = path;
            mStatus = new VolatileBox<>(true);
            VolatileBox<Boolean> previous = mDownloadStatuses.put(mUrl, mStatus);
            if (previous != null)
                previous.set(false);
            mProgress = PublishSubject.create();
        }

        private void startImpl(ResponseBody body) throws IOException {
            byte[] buffer = new byte[4096];
            mFileOutputStream = new FileOutputStream(mPath);
            mInputStream = body.byteStream();
            long total = body.contentLength();
            long read = 0;
            while (true) {
                if (!mStatus.get()) {
                    onCancel();
                    return;
                }
                int len = mInputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                read += len;
                mFileOutputStream.write(buffer, 0, len);
                if (total > 0) {
                    mProgress.onNext((int) (100 * read / total));
                }
            }
            mProgress.onComplete();
            recycle();
        }

        public void start(ResponseBody body) {
            try {
                PFiles.ensureDir(mPath);
                startImpl(body);
            } catch (Exception e) {
                mProgress.onError(e);
            }
        }

        private void onCancel() throws IOException {
            recycle();
            // TODO: 2017/12/6 notify?
        }

        public void recycle() {
            mDownloadStatuses.remove(mUrl);
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException ignored) {

                }
            }
            if (mFileOutputStream != null) {
                try {
                    mFileOutputStream.close();
                } catch (IOException ignored) {
                }
            }

        }

        public PublishSubject<Integer> progress() {
            return mProgress;
        }


    }
}
