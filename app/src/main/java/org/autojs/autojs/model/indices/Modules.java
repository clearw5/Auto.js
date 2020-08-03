package org.autojs.autojs.model.indices;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/12/9.
 */

public class Modules {

    private static final Type MODULE_LIST_TYPE = new TypeToken<List<Module>>() {

    }.getType();
    private static final String MODULES_JSON_PATH = "indices/all.json";
    private static Modules sInstance = new Modules();

    private List<Module> mModules;

    private List<Module> loadModulesFrom(InputStream inputStream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(inputStream), MODULE_LIST_TYPE);
    }

    public Observable<List<Module>> getModules(Context context) {
        if (mModules != null)
            return Observable.just(mModules);
        return Observable.fromCallable(() -> loadModulesFrom(context.getAssets().open(MODULES_JSON_PATH)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(modules -> mModules = modules);
    }

    public static Modules getInstance() {
        return sInstance;
    }
}
