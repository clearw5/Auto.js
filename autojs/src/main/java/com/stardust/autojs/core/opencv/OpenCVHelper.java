package com.stardust.autojs.core.opencv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


/**
 * Created by Stardust on 2018/4/2.
 */

public class OpenCVHelper {

    public interface InitializeCallback {
        void onInitFinish();
    }

    private static final String LOG_TAG = "OpenCVHelper";
    private static boolean sInitialized = false;

    public static MatOfPoint newMatOfPoint(Mat mat){
        return new MatOfPoint(mat);
    }

    public static void release(@Nullable MatOfPoint mat) {
        if (mat == null)
            return;
        mat.release();
    }

    public static void release(@Nullable Mat mat) {
        if (mat == null)
            return;
        mat.release();
    }

    public synchronized static boolean isInitialized() {
        return sInitialized;
    }

    public synchronized static void initIfNeeded(Context context, InitializeCallback callback) {
        if (sInitialized) {
            callback.onInitFinish();
            return;
        }
        sInitialized = true;
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, context.getApplicationContext(), new LoaderCallback(context) {

            @Override
            protected void finish() {
                callback.onInitFinish();
            }
        });
    }


    public static class LoaderCallback implements LoaderCallbackInterface {
        private Context mContext;

        public LoaderCallback(Context context) {
            this.mContext = context;
        }

        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    finish();
                    break;
                case 1:
                default:
                    Log.e(LOG_TAG, "OpenCV loading failed!");
                    DialogUtils.showDialog(new MaterialDialog.Builder(mContext)
                            .title("OpenCV error")
                            .content("OpenCV was not initialised correctly. Application will be shut down")
                            .cancelable(false)
                            .positiveText("OK")
                            .onPositive((dialog, which) -> finish())
                            .build());
                    break;
                case 2:
                    Log.e(LOG_TAG, "Package installation failed!");
                    DialogUtils.showDialog(new MaterialDialog.Builder(mContext)
                            .title("OpenCV Manager")
                            .content("Package installation failed!")
                            .cancelable(false)
                            .positiveText("OK")
                            .onPositive((dialog, which) -> finish())
                            .build());
                    break;
                case 3:
                    Log.d(LOG_TAG, "OpenCV library instalation was canceled by user");
                    finish();
                    break;
                case 4:
                    Log.d(LOG_TAG, "OpenCV Manager Service is uncompatible with this app!");
                    DialogUtils.showDialog(new MaterialDialog.Builder(mContext)
                            .title("OpenCV Manager")
                            .content("OpenCV Manager service is incompatible with this app. Try to update it via Google Play.")
                            .cancelable(false)
                            .positiveText("OK")
                            .onPositive((dialog, which) -> finish())
                            .build());
            }

        }

        public void onPackageInstall(int operation, final InstallCallbackInterface callback) {
            switch (operation) {
                case 0:
                    DialogUtils.showDialog(new MaterialDialog.Builder(mContext)
                            .title("Package not found")
                            .content(callback.getPackageName() + " package was not found! Try to install it?")
                            .cancelable(false)
                            .positiveText("Yes")
                            .onPositive((dialog, which) -> callback.install())
                            .negativeText("No")
                            .onNegative(((dialog, which) -> callback.cancel()))
                            .build());
                    break;
                case 1:
                    DialogUtils.showDialog(new MaterialDialog.Builder(mContext)
                            .title("OpenCV is not ready")
                            .content("Installation is in progress. Wait or exit?")
                            .cancelable(false)
                            .positiveText("Wait")
                            .onPositive((dialog, which) -> callback.wait_install())
                            .negativeText("Exit")
                            .onNegative(((dialog, which) -> callback.cancel()))
                            .build());
                default:
                    finish();
            }

        }

        protected void finish() {
        }
    }

}
