package org.autojs.autojs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.multidex.MultiDexApplication
import android.view.View
import android.widget.ImageView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.evernote.android.job.JobRequest
import com.flurry.android.FlurryAgent
import com.squareup.leakcanary.LeakCanary
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.ui.inflater.ImageLoader
import com.stardust.autojs.core.ui.inflater.util.Drawables
import com.stardust.theme.ThemeColor
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport
import org.autojs.autojs.autojs.AutoJs
import org.autojs.autojs.autojs.key.GlobalKeyObserver
import org.autojs.autojs.external.receiver.DynamicBroadcastReceivers
import org.autojs.autojs.theme.ThemeColorManagerCompat
import org.autojs.autojs.timing.TimedTaskManager
import org.autojs.autojs.timing.TimedTaskScheduler
import org.autojs.autojs.tool.CrashHandler
import org.autojs.autojs.ui.error.ErrorReportActivity
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Stardust on 2017/1/27.
 */

class App : MultiDexApplication() {
    lateinit var dynamicBroadcastReceivers: DynamicBroadcastReceivers
        private set

    override fun onCreate() {
        super.onCreate()
        GlobalAppContext.set(this)
        instance = WeakReference(this)
        setUpStaticsTool()
        setUpDebugEnvironment()
        init()
    }

    private fun setUpStaticsTool() {
        if (BuildConfig.DEBUG)
            return
        FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .build(this, "D42MH48ZN4PJC5TKNYZD")
    }

    private fun setUpDebugEnvironment() {
        Bugly.isDev = false
        val crashHandler = CrashHandler(ErrorReportActivity::class.java)

        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.setCrashHandleCallback(crashHandler)

        CrashReport.initCrashReport(applicationContext, BUGLY_APP_ID, false, strategy)

        crashHandler.setBuglyHandler(Thread.getDefaultUncaughtExceptionHandler())
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        //LeakCanary.install(this);

    }

    private fun init() {
        ThemeColorManagerCompat.init(this, ThemeColor(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.colorPrimaryDark), resources.getColor(R.color.colorAccent)))
        AutoJs.initInstance(this)
        if (Pref.isRunningVolumeControlEnabled()) {
            GlobalKeyObserver.init()
        }
        setupDrawableImageLoader()
        TimedTaskScheduler.init(this)
        initDynamicBroadcastReceivers()
    }

    @SuppressLint("CheckResult")
    private fun initDynamicBroadcastReceivers() {
        dynamicBroadcastReceivers = DynamicBroadcastReceivers(this)
        val localActions = ArrayList<String>()
        val actions = ArrayList<String>()
        TimedTaskManager.getInstance().allIntentTasks
                .filter { task -> task.action != null }
                .doOnComplete {
                    if (localActions.isNotEmpty()) {
                        dynamicBroadcastReceivers.register(localActions, true)
                    }
                    if (actions.isNotEmpty()) {
                        dynamicBroadcastReceivers.register(actions, false)
                    }
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent(
                            DynamicBroadcastReceivers.ACTION_STARTUP
                    ))
                }
                .subscribe({
                    if (it.isLocal) {
                        localActions.add(it.action)
                    } else {
                        actions.add(it.action)
                    }
                }, { it.printStackTrace() })


    }

    private fun setupDrawableImageLoader() {
        Drawables.setDefaultImageLoader(object : ImageLoader {
            override fun loadInto(imageView: ImageView, uri: Uri) {
                Glide.with(imageView)
                        .load(uri)
                        .into(imageView)
            }

            override fun loadIntoBackground(view: View, uri: Uri) {
                Glide.with(view)
                        .load(uri)
                        .into(object : SimpleTarget<Drawable>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                view.background = resource
                            }
                        })
            }

            override fun load(view: View, uri: Uri): Drawable {
                throw UnsupportedOperationException()
            }

            override fun load(view: View, uri: Uri, drawableCallback: ImageLoader.DrawableCallback) {
                Glide.with(view)
                        .load(uri)
                        .into(object : SimpleTarget<Drawable>() {
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                drawableCallback.onLoaded(resource)
                            }
                        })
            }

            override fun load(view: View, uri: Uri, bitmapCallback: ImageLoader.BitmapCallback) {
                Glide.with(view)
                        .asBitmap()
                        .load(uri)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                bitmapCallback.onLoaded(resource)
                            }
                        })
            }
        })
    }

    companion object {

        private val TAG = "App"
        private val BUGLY_APP_ID = "19b3607b53"

        private lateinit var instance: WeakReference<App>

        val app: App
            get() = instance.get()!!
    }


}
