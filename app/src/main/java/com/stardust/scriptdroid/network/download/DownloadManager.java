package com.stardust.scriptdroid.network.download;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Status;
import zlc.season.rxdownload3.core.Succeed;

import static android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR;
import static android.app.DownloadManager.COLUMN_STATUS;
import static android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES;
import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;

/**
 * Created by Stardust on 2017/10/20.
 */

public class DownloadManager {

    private static DownloadManager sInstance;

    private android.app.DownloadManager mManager;
    private Context mContext;
    private Handler mHandler;


    public DownloadManager(Context context) {
        mContext = context;
        mHandler = new Handler();
        mManager = (android.app.DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static DownloadManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DownloadManager(context);
        }
        return sInstance;
    }


    public static String parseFileNameLocally(String url) {
        int i = url.lastIndexOf('-');
        if (i < 0) {
            i = url.lastIndexOf('/');
        }
        return url.substring(i + 1);
    }

    public Observable<Integer> download(String url, String path) {
        File file = new File(path);
        Mission mission = new Mission(url, file.getName(), file.getParent());
        PublishSubject<Integer> progress = PublishSubject.create();
        RxDownload.INSTANCE.create(mission)
                .subscribe(status -> {
                    int p = (int) Math.floor((float) status.getDownloadSize() / status.getTotalSize() * 100);
                    progress.onNext(p);
                    if (status instanceof Succeed) {
                        progress.onComplete();
                        RxDownload.INSTANCE.delete(mission);
                    } else if (status instanceof Failed) {
                        progress.onError(((Failed) status).getThrowable());
                    }
                });
        RxDownload.INSTANCE.start(mission).subscribe();
        return progress;
    }

    public Observable<Integer> download$(String url, String path) {
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(url));
        request.setDestinationUri(Uri.parse("file://" + path));
        long id = mManager.enqueue(request);
        PublishSubject<Integer> downloadProgress = PublishSubject.create();
        Disposable disposable = Observable.interval(0, 200, TimeUnit.MILLISECONDS)
                .subscribe(t -> {
                    int progress = getDownloadProgress(id);
                    if (progress < 0) {
                        downloadProgress.onError(new DownloadFailedException(url, path));
                    }
                    downloadProgress.onNext(progress);
                    if (progress == 100) {
                        downloadProgress.onComplete();
                    }
                });
        downloadProgress.doOnComplete(disposable::dispose);
        return downloadProgress;
    }

    private int getDownloadProgress(long id) {
        Cursor cursor = mManager.query(new android.app.DownloadManager.Query()
                .setFilterById(id));
        if (!cursor.moveToFirst()) {
            return -1;
        }
        int total = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_SIZE_BYTES));
        int downloaded = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BYTES_DOWNLOADED_SO_FAR));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
        if (status == STATUS_SUCCESSFUL) {
            return 100;
        } else if (status == STATUS_FAILED) {
            return -1;
        }
        return (int) Math.floor((float) downloaded / total * 100);
    }

    public void cancelDownload(String url) {
        RxDownload.INSTANCE.stop(url);
        RxDownload.INSTANCE.delete(url);
    }
}
