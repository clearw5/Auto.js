package com.stardust.scriptdroid;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flurry.android.FlurryAgent;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.runtime.DirectModelNotifier;
import com.squareup.leakcanary.LeakCanary;
import com.stardust.autojs.core.ui.inflater.ImageLoader;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.autojs.key.GlobalKeyObserver;
import com.stardust.scriptdroid.network.GlideApp;
import com.stardust.scriptdroid.storage.database.TimedTaskDatabase;
import com.stardust.scriptdroid.timing.TimedTaskScheduler;
import com.stardust.scriptdroid.tool.CrashHandler;
import com.stardust.scriptdroid.ui.error.ErrorReportActivity;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.util.UiHandler;


import java.lang.ref.WeakReference;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends MultiDexApplication {

    private static final String TAG = "App";

    private static WeakReference<App> instance;
    private UiHandler mUiHandler;

    public static App getApp() {
        return instance.get();
    }

    public void onCreate() {
        super.onCreate();
        instance = new WeakReference<>(this);
        mUiHandler = new UiHandler(this);
        setUpStaticsTool();
        setUpDebugEnvironment();
        init();
    }

    private void setUpStaticsTool() {
        if (BuildConfig.DEBUG)
            return;
        new FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .build(this, "D42MH48ZN4PJC5TKNYZD");
    }


    private void setUpDebugEnvironment() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(ErrorReportActivity.class));
    }

    private void init() {
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(TimedTaskDatabase.class)
                        .modelNotifier(DirectModelNotifier.get())
                        .build())
                .build());
        ThemeColorManager.setDefaultThemeColor(new ThemeColor(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent)));
        ThemeColorManager.init(this);
        AutoJs.initInstance(this);
        GlobalKeyObserver.getSingleton();
        setupDrawableImageLoader();
        TimedTaskScheduler.setupRepeating(this);
    }

    private void setupDrawableImageLoader() {
        Drawables.setDefaultImageLoader(new ImageLoader() {
            @Override
            public void loadInto(ImageView imageView, Uri uri) {
                GlideApp.with(App.this)
                        .load(uri)
                        .into(imageView);
            }

            @Override
            public void loadIntoBackground(View view, Uri uri) {
                GlideApp.with(App.this)
                        .load(uri)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                view.setBackground(resource);
                            }
                        });
            }

            @Override
            public Drawable load(View view, Uri uri) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void load(View view, Uri uri, DrawableCallback drawableCallback) {
                GlideApp.with(App.this)
                        .load(uri)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                drawableCallback.onLoaded(resource);
                            }
                        });
            }

            @Override
            public void load(View view, Uri uri, BitmapCallback bitmapCallback) {
                GlideApp.with(App.this)
                        .asBitmap()
                        .load(uri)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                bitmapCallback.onLoaded(resource);
                            }
                        });
            }
        });
    }


    public UiHandler getUiHandler() {
        return mUiHandler;
    }
}
