package org.autojs.autojs;

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
import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.ui.inflater.ImageLoader;
import com.stardust.autojs.core.ui.inflater.util.Drawables;

import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.autojs.key.GlobalKeyObserver;
import org.autojs.autojs.network.GlideApp;
import org.autojs.autojs.storage.database.IntentTaskDatabase;
import org.autojs.autojs.storage.database.TimedTaskDatabase;
import org.autojs.autojs.timing.TimedTaskScheduler;
import org.autojs.autojs.tool.CrashHandler;
import org.autojs.autojs.ui.error.ErrorReportActivity;

import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.tencent.bugly.crashreport.CrashReport;


import java.lang.ref.WeakReference;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends MultiDexApplication {

    private static final String TAG = "App";
    private static final String BUGLY_APP_ID = "19b3607b53";

    private static WeakReference<App> instance;

    public static App getApp() {
        return instance.get();
    }

    public void onCreate() {
        super.onCreate();
        GlobalAppContext.set(this);
        instance = new WeakReference<>(this);
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
        CrashHandler crashHandler = new CrashHandler(ErrorReportActivity.class);

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(crashHandler);

        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APP_ID, false, strategy);

        crashHandler.setBuglyHandler(Thread.getDefaultUncaughtExceptionHandler());
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    private void init() {
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(TimedTaskDatabase.class)
                        .modelNotifier(DirectModelNotifier.get())
                        .build())
                .addDatabaseConfig(DatabaseConfig.builder(IntentTaskDatabase.class)
                        .modelNotifier(DirectModelNotifier.get())
                        .build())
                .build());
        ThemeColorManager.setDefaultThemeColor(new ThemeColor(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent)));
        ThemeColorManager.init(this);
        AutoJs.initInstance(this);
        if (Pref.isRunningVolumeControlEnabled()) {
            GlobalKeyObserver.init();
        }
        setupDrawableImageLoader();
        TimedTaskScheduler.checkTasksRepeatedlyIfNeeded(this);
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


}
