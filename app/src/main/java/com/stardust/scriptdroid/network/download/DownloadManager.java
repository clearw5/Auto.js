package com.stardust.scriptdroid.network.download;

import android.content.Context;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Mission;
import zlc.season.rxdownload3.core.Succeed;

/**
 * Created by Stardust on 2017/10/20.
 */

public class DownloadManager {

    private static DownloadManager sInstance;

    private Context mContext;


    public DownloadManager(Context context) {
        mContext = context;
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
                    if (status.getTotalSize() > 0) {
                        int p = (int) Math.floor((float) status.getDownloadSize() / status.getTotalSize() * 100);
                        progress.onNext(p);
                    }
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

    public void cancelDownload(String url) {
        RxDownload.INSTANCE.stop(url);
        RxDownload.INSTANCE.delete(url);
    }
}
