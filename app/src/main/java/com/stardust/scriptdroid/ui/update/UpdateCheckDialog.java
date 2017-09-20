package com.stardust.scriptdroid.ui.update;

import android.content.Context;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.network.VersionService;
import com.stardust.scriptdroid.network.entity.VersionInfo;
import com.stardust.scriptdroid.tool.SimpleObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;

/**
 * Created by Stardust on 2017/9/20.
 */

public class UpdateCheckDialog {

    private MaterialDialog mProgress;
    private Context mContext;

    public UpdateCheckDialog(Context context) {
        mProgress = new MaterialDialog.Builder(context)
                .progress(true, 0)
                .content(R.string.text_checking_update)
                .build();
    }

    public void show() {
        mProgress.show();
        VersionService.getInstance()
                .checkForUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<VersionInfo>() {
                    @Override
                    public void onNext(@NonNull VersionInfo versionInfo) {
                        mProgress.dismiss();
                        if (versionInfo.isNewer()) {
                            new UpdateInfoDialogBuilder(mContext, versionInfo)
                                    .show();
                        } else {
                            Toast.makeText(App.getApp(), R.string.text_is_latest_version, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        mProgress.dismiss();
                        Toast.makeText(App.getApp(), R.string.text_check_update_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
