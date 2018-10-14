package org.autojs.autojs.model.indices;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.autojs.autojs.tool.Observers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AndroidClassIndices {

    static class LoadException extends RuntimeException {

        LoadException(Throwable cause) {
            super(cause);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static AndroidClassIndices sInstance;

    private static Type ANDROID_CLASS_LIST_TYPE = new TypeToken<List<AndroidClass>>() {
    }.getType();
    //packageName -> package classes
    private LinkedHashMap<String, ArrayList<ClassSearchingItem>> mPackages = new LinkedHashMap<>();
    private Context mContext;
    private ExecutorService mSingleThreadExecutor = new ThreadPoolExecutor(1, 1,
            2, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    private Throwable mLoadThrowable;


    protected AndroidClassIndices(Context context) {
        mContext = context.getApplicationContext();
        load("indices/all_android_classes.json");
    }

    public static AndroidClassIndices getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AndroidClassIndices(context);
        }
        return sInstance;
    }

    public Single<List<ClassSearchingItem>> search(String keywords) {
        return Single.fromCallable(() -> {
            if (mPackages.isEmpty() && mLoadThrowable != null) {
                throw new LoadException(mLoadThrowable);
            }
            return mPackages.values();
        })
                .map(packages -> search(packages, keywords))
                .subscribeOn(Schedulers.from(mSingleThreadExecutor));
    }

    private List<ClassSearchingItem> search(Collection<ArrayList<ClassSearchingItem>> packages, String keywords) {
        List<ClassSearchingItem> result = new ArrayList<>();
        if (TextUtils.isEmpty(keywords)) {
            for (ArrayList<ClassSearchingItem> packageClasses : packages) {
                result.addAll(packageClasses);
            }
        } else {
            for (ArrayList<ClassSearchingItem> packageClasses : packages) {
                for (ClassSearchingItem item : packageClasses) {
                    if (item.matches(keywords)) {
                        result.add(item);
                    }
                }
                Collections.sort(result);
            }
        }
        return result;
    }

    @SuppressLint("CheckResult")
    private void load(String assetsPath) {
        Observable.just(assetsPath)
                .map(path -> mContext.getAssets().open(path))
                .map(InputStreamReader::new)
                .doOnNext(this::load)
                .subscribeOn(Schedulers.from(mSingleThreadExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.emptyConsumer(), t -> {
                    mLoadThrowable = t;
                    t.printStackTrace();
                });
    }


    private void load(Reader reader) throws IOException {
        try {
            Gson gson = new Gson();
            ArrayList<AndroidClass> classes = gson.fromJson(reader, ANDROID_CLASS_LIST_TYPE);
            load(classes);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            reader.close();
        }
    }

    private void load(ArrayList<AndroidClass> classes) {
        mPackages.clear();
        for (AndroidClass clazz : classes) {
            String packageName = clazz.getPackageName();
            ArrayList<ClassSearchingItem> packageClasses = mPackages.get(packageName);
            if (packageClasses == null) {
                packageClasses = new ArrayList<>();
                mPackages.put(packageName, packageClasses);
                packageClasses.add(new ClassSearchingItem.PackageItem(packageName));
            }
            packageClasses.add(new ClassSearchingItem.ClassItem(clazz));
        }
    }

}
