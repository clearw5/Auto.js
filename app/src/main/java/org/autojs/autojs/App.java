package org.autojs.autojs;

import android.annotation.SuppressLint;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flurry.android.FlurryAgent;
import com.squareup.leakcanary.LeakCanary;
import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.ui.inflater.ImageLoader;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.autojs.key.GlobalKeyObserver;
import org.autojs.autojs.external.receiver.DynamicBroadcastReceivers;
import org.autojs.autojs.network.GlideApp;
import org.autojs.autojs.timing.IntentTask;
import org.autojs.autojs.timing.TimedTaskManager;
import org.autojs.autojs.timing.TimedTaskScheduler;
import org.autojs.autojs.tool.CrashHandler;
import org.autojs.autojs.ui.error.ErrorReportActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Stardust on 2017/1/27.
 */

public class App extends MultiDexApplication {

    private static final String TAG = "App";
    private static final String BUGLY_APP_ID = "19b3607b53";

    private static WeakReference<App> instance;
    private DynamicBroadcastReceivers mDynamicBroadcastReceivers;

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

    public DynamicBroadcastReceivers getDynamicBroadcastReceivers() {
        return mDynamicBroadcastReceivers;
    }

    private void setUpStaticsTool() {
        if (BuildConfig.DEBUG)
            return;
        new FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .build(this, "D42MH48ZN4PJC5TKNYZD");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void setUpDebugEnvironment() {
        Bugly.isDev = false;
        CrashHandler crashHandler = new CrashHandler(ErrorReportActivity.class);

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(crashHandler);

        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APP_ID, false, strategy);

        crashHandler.setBuglyHandler(Thread.getDefaultUncaughtExceptionHandler());
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        //LeakCanary.install(this);

    }

    private void init() {
        ThemeColorManager.setDefaultThemeColor(new ThemeColor(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent)));
        ThemeColorManager.init(this);
        AutoJs.initInstance(this);
        if (Pref.isRunningVolumeControlEnabled()) {
            GlobalKeyObserver.init();
        }
        setupDrawableImageLoader();
        TimedTaskScheduler.checkTasksRepeatedlyIfNeeded(this);
        initDynamicBroadcastReceivers();

    }

    @SuppressLint("CheckResult")
    private void initDynamicBroadcastReceivers() {
        mDynamicBroadcastReceivers = new DynamicBroadcastReceivers(this);
        TimedTaskManager.getInstance().getAllIntentTasks()
                .filter(task -> task.getAction() != null)
                .map(IntentTask::getAction)
                .collectInto(new ArrayList<String>(), ArrayList::add)
                .subscribe(list -> mDynamicBroadcastReceivers.register(list),
                        Throwable::printStackTrace);
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
