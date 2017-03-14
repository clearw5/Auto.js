package com.stardust.scriptdroid.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.sample.SampleFileManager;
import com.stardust.scriptdroid.tool.IntentTool;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.error.IssueReportActivity;
import com.stardust.util.MapEntries;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.ui.main.MainActivity;
import com.stardust.theme.app.ColorSelectActivity;
import com.stardust.theme.util.ListBuilder;

import java.util.List;
import java.util.Map;

import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;

/**
 * Created by Stardust on 2017/2/2.
 */

public class SettingsActivity extends BaseActivity {

    private static final List<ColorSelectActivity.ColorItem> COLOR_ITEMS;

    static {
        COLOR_ITEMS = new ListBuilder<ColorSelectActivity.ColorItem>()
                .add(new ColorSelectActivity.ColorItem("基佬红", 0xFFF44336))
                .add(new ColorSelectActivity.ColorItem("基佬粉", 0xFFE91E63))
                .add(new ColorSelectActivity.ColorItem("基佬紫", 0xFF9C27B0))
                .add(new ColorSelectActivity.ColorItem("基深紫", 0xFF673AB7))
                .add(new ColorSelectActivity.ColorItem("基靛蓝", 0xFF3F51B5))
                .add(new ColorSelectActivity.ColorItem("基佬蓝", 0xFF2196F3))
                .add(new ColorSelectActivity.ColorItem("基亮蓝", 0xFF03A9F4))
                .add(new ColorSelectActivity.ColorItem("基蓝绿", 0xFF00BCD4))
                .add(new ColorSelectActivity.ColorItem("基佬青", 0xFF009688))
                .add(new ColorSelectActivity.ColorItem("基佬绿", 0xFF4CAF50))
                .add(new ColorSelectActivity.ColorItem("基亮绿", 0xFF8BC34A))
                .add(new ColorSelectActivity.ColorItem("基黄绿", 0xFFCDDC39))
                .add(new ColorSelectActivity.ColorItem("基佬黄", 0xFFFFEB3B))
                .add(new ColorSelectActivity.ColorItem("基琥珀", 0xFFFFC107))
                .add(new ColorSelectActivity.ColorItem("基佬橙", 0xFFFF9800))
                .add(new ColorSelectActivity.ColorItem("基深橙", 0xFFFF5722))
                .add(new ColorSelectActivity.ColorItem("基佬棕", 0xFF795548))
                .add(new ColorSelectActivity.ColorItem("基佬灰", 0xFF9E9E9E))
                .add(new ColorSelectActivity.ColorItem("基南灰", 0xFF607D8B))
                .list();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUI();
    }

    private void setUpUI() {
        setContentView(R.layout.activity_settings);
        setUpToolbar();
        getFragmentManager().beginTransaction().replace(R.id.fragment_setting, new PreferenceFragment()).commit();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(R.string.text_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static class PreferenceFragment extends android.preference.PreferenceFragment {

        private Map<String, Runnable> ACTION_MAP;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();
            ACTION_MAP = new MapEntries<String, Runnable>()
                    .entry(getString(R.string.text_theme_color), new Runnable() {
                        @Override
                        public void run() {
                            ColorSelectActivity.startColorSelect(getActivity(), getString(R.string.mt_color_picker_title), COLOR_ITEMS);
                        }
                    })
                    .entry(getString(R.string.text_reset_background), new Runnable() {
                        @Override
                        public void run() {
                            Pref.def().edit().putString(Pref.KEY_APP_BAR_IMAGE_PATH, null)
                                    .putString(Pref.KEY_DRAWER_HEADER_IMAGE_PATH, null)
                                    .apply();
                            Toast.makeText(getActivity(), R.string.text_restart_app_to_apply, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .entry(getString(R.string.text_issue_report), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), IssueReportActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    })
                    .entry(getString(R.string.text_join_qq_group), new Runnable() {
                        @Override
                        public void run() {
                            if (!IntentTool.joinQQGroup(getActivity(), "vjHXzZlpGcXNe-YEWzQ85mm_z8y-curC")) {
                                Toast.makeText(getActivity(), R.string.text_mobile_qq_not_installed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .entry(getString(R.string.text_about), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), AboutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    })
                    .entry(getString(R.string.text_licenses), new Runnable() {
                        @Override
                        public void run() {
                            showLicenseDialog();
                        }
                    })
                    .map();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            Runnable action = ACTION_MAP.get(preference.getTitle().toString());
            if (action != null) {
                action.run();
                return true;
            } else {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            }
        }

        private void showLicenseDialog() {
            LicenseResolver.registerLicense(MozillaPublicLicense20.instance);
            new LicensesDialog.Builder(getActivity())
                    .setNotices(R.raw.licenses)
                    .setIncludeOwnLicense(true)
                    .build()
                    .showAppCompat();
        }

        public static class MozillaPublicLicense20 extends License {

            public static MozillaPublicLicense20 instance = new MozillaPublicLicense20();

            @Override
            public String getName() {
                return "Mozilla Public License 2.0";
            }

            @Override
            public String readSummaryTextFromResources(Context context) {
                return getContent(context, R.raw.mpl_20_summary);
            }

            @Override
            public String readFullTextFromResources(Context context) {
                return getContent(context, R.raw.mpl_20_full);
            }

            @Override
            public String getVersion() {
                return "2.0";
            }

            @Override
            public String getUrl() {
                return "https://www.mozilla.org/en-US/MPL/2.0/";
            }
        }

    }
}
