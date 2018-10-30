package com.stardust.autojs.core.opencv;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

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
    private static boolean mInitialized = false;

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

    public static void initIfNeeded(Activity activity, InitializeCallback callback) {
        if (mInitialized) {
            callback.onInitFinish();
            return;
        }
        mInitialized = true;
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, activity.getApplicationContext(), new LoaderCallback(activity) {

            @Override
            protected void finish() {
                callback.onInitFinish();
            }
        });
    }


    public static class LoaderCallback implements LoaderCallbackInterface {
        private Activity mActivity;

        public LoaderCallback(Activity activity) {
            this.mActivity = activity;
        }

        public void onManagerConnected(int status) {
            switch (status) {
                case 0:
                    finish();
                    break;
                case 1:
                default:
                    Log.e(LOG_TAG, "OpenCV loading failed!");
                    new MaterialDialog.Builder(mActivity)
                            .title("OpenCV error")
                            .content("OpenCV was not initialised correctly. Application will be shut down")
                            .cancelable(false)
                            .positiveText("OK")
                            .onPositive((dialog, which) -> finish())
                            .show();
                    break;
                case 2:
                    Log.e(LOG_TAG, "Package installation failed!");
                    new MaterialDialog.Builder(mActivity)
                            .title("OpenCV Manager")
                            .content("Package installation failed!")
                            .cancelable(false)
                            .positiveText("OK")
                            .onPositive((dialog, which) -> finish())
                            .show();
                    break;
                case 3:
                    Log.d(LOG_TAG, "OpenCV library instalation was canceled by user");
                    finish();
                    break;
                case 4:
                    Log.d(LOG_TAG, "OpenCV Manager Service is uncompatible with this app!");
                    new MaterialDialog.Builder(mActivity)
                            .title("OpenCV Manager")
                            .content("OpenCV Manager service is incompatible with this app. Try to update it via Google Play.")
                            .cancelable(false)
                            .positiveText("OK")
                            .onPositive((dialog, which) -> finish())
                            .show();
            }

        }

        public void onPackageInstall(int operation, final InstallCallbackInterface callback) {
            switch (operation) {
                case 0:
                    new MaterialDialog.Builder(mActivity)
                            .title("Package not found")
                            .content(callback.getPackageName() + " package was not found! Try to install it?")
                            .cancelable(false)
                            .positiveText("Yes")
                            .onPositive((dialog, which) -> callback.install())
                            .negativeText("No")
                            .onNegative(((dialog, which) -> callback.cancel()))
                            .show();
                    break;
                case 1:
                    new MaterialDialog.Builder(mActivity)
                            .title("OpenCV is not ready")
                            .content("Installation is in progress. Wait or exit?")
                            .cancelable(false)
                            .positiveText("Wait")
                            .onPositive((dialog, which) -> callback.wait_install())
                            .negativeText("Exit")
                            .onNegative(((dialog, which) -> callback.cancel()))
                            .show();
                default:
                    finish();
            }

        }

        protected void finish() {
            mActivity.finish();
        }
    }

}
