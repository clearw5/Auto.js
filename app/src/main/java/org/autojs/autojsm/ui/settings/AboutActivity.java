package org.autojs.autojsm.ui.settings;

import android.annotation.SuppressLint;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojsm.autojs.AutoJs;
import org.autojs.autojsm.timing.TimedTaskManager;
import org.autojs.autojsm.tool.IntentTool;
import org.autojs.autojsm.ui.BaseActivity;
import org.autojs.autojsm.theme.dialog.ThemeColorMaterialDialogBuilder;
import com.stardust.util.IntentUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.autojs.autojsm.BuildConfig;
import org.autojs.autojsm.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import androidx.work.WorkInfo;
import androidx.work.WorkManager;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    @ViewById(R.id.version)
    TextView mVersion;

    private int mLolClickCount = 0;


    @AfterViews
    void setUpViews() {
        setVersionName();
        setToolbarAsBack(getString(R.string.text_about));
    }

    @SuppressLint("SetTextI18n")
    private void setVersionName() {
        mVersion.setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Click(R.id.github)
    void openGitHub() {
        IntentTool.browse(this, getString(R.string.my_github));
    }

    @Click(R.id.new_github)
    void openModifiedGitHub() {
        IntentTool.browse(this, getString(R.string.new_github_repo));
    }

    @Click(R.id.qq)
    void openQQToChatWithMe() {
        String qq = getString(R.string.qq);
        if (!IntentUtil.chatWithQQ(this, qq)) {
            Toast.makeText(this, R.string.text_mobile_qq_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.icon_container)
    @SuppressWarnings("CheckResult")
    void showDebugInfo() {
        try {
            List<WorkInfo> workInfos = WorkManager.getInstance(getApplicationContext()).getWorkInfosForUniqueWork("checkTasks").get();
            if (workInfos != null && workInfos.size() > 0) {
                for (WorkInfo workInfo : workInfos) {
                    AutoJs.getInstance().debugInfo("checkTasks work:" + workInfo.toString());
                }
            } else {
                AutoJs.getInstance().debugInfo("checkTasks work is not running");
            }
        } catch (Exception e) {

        }

        TimedTaskManager.getInstance().getAllTasks().forEach(task -> {
            try {
                List<WorkInfo> workInfos = WorkManager.getInstance(getApplicationContext()).getWorkInfosByTag(String.valueOf(task.getId())).get();
                if (workInfos != null && workInfos.size() > 0) {
                    for (WorkInfo workInfo : workInfos) {
                        AutoJs.getInstance().debugInfo("work for taskId: " + task.getId() + " workInfo: " + workInfo.toString());
                    }
                } else {
                    AutoJs.getInstance().debugInfo("work for taskId:" + task.getId() + " is not running");
                }
            } catch (Exception e) {

            }
        });
    }

    @Click(R.id.email)
    void openEmailToSendMe() {
        String email = getString(R.string.email);
        IntentUtil.sendMailTo(this, email);
    }


//    @Click(R.id.share)
//    void share() {
//        IntentUtil.shareText(this, getString(R.string.share_app));
//    }

    @Click(R.id.icon)
    void lol() {
        mLolClickCount++;
        //Toast.makeText(this, R.string.text_lll, Toast.LENGTH_LONG).show();
        if (mLolClickCount >= 5) {
            crashTest();
            //showEasterEgg();
        }
    }

    private void showEasterEgg() {
        new MaterialDialog.Builder(this)
                .customView(R.layout.paint_layout, false)
                .show();
    }

    private void crashTest() {
        new ThemeColorMaterialDialogBuilder(this)
                .title("Crash Test")
                .positiveText("Crash")
                .onPositive((dialog, which) -> {
                    CrashReport.testJavaCrash();
                }).show();
    }

    @Click(R.id.developer)
    void hhh() {
        Toast.makeText(this, R.string.text_it_is_the_developer_of_app, Toast.LENGTH_LONG).show();
    }

    @Click(R.id.modifier)
    void hhhh() {
        Toast.makeText(this, R.string.text_it_is_the_modifier_of_app, Toast.LENGTH_LONG).show();
    }


}
