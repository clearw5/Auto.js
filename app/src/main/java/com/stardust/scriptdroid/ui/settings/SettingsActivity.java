package com.stardust.scriptdroid.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.stardust.scriptdroid.network.VersionService;
import com.stardust.scriptdroid.ui.BaseActivity;
import com.stardust.scriptdroid.ui.error.IssueReporterActivity;
import com.stardust.scriptdroid.ui.splash.SplashActivity;
import com.stardust.scriptdroid.ui.splash.SplashActivity_;
import com.stardust.scriptdroid.ui.update.UpdateCheckDialog;
import com.stardust.theme.preference.ThemeColorPreferenceFragment;
import com.stardust.util.IntentUtil;
import com.stardust.util.MapEntries;
import com.stardust.scriptdroid.R;
import com.stardust.theme.app.ColorSelectActivity;
import com.stardust.theme.util.ListBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    private static final List<Pair<Integer, Integer>> COLOR_ITEMS = new ListBuilder<Pair<Integer, Integer>>()
            .add(new Pair<>(R.color.theme_color_red, R.string.theme_color_red))
            .add(new Pair<>(R.color.theme_color_pink, R.string.theme_color_pink))
            .add(new Pair<>(R.color.theme_color_purple, R.string.theme_color_purple))
            .add(new Pair<>(R.color.theme_color_dark_purple, R.string.theme_color_dark_purple))
            .add(new Pair<>(R.color.theme_color_indigo, R.string.theme_color_indigo))
            .add(new Pair<>(R.color.theme_color_blue, R.string.theme_color_blue))
            .add(new Pair<>(R.color.theme_color_light_blue, R.string.theme_color_light_blue))
            .add(new Pair<>(R.color.theme_color_blue_green, R.string.theme_color_blue_green))
            .add(new Pair<>(R.color.theme_color_cyan, R.string.theme_color_cyan))
            .add(new Pair<>(R.color.theme_color_green, R.string.theme_color_green))
            .add(new Pair<>(R.color.theme_color_light_green, R.string.theme_color_light_green))
            .add(new Pair<>(R.color.theme_color_yellow_green, R.string.theme_color_yellow_green))
            .add(new Pair<>(R.color.theme_color_yellow, R.string.theme_color_yellow))
            .add(new Pair<>(R.color.theme_color_amber, R.string.theme_color_amber))
            .add(new Pair<>(R.color.theme_color_orange, R.string.theme_color_orange))
            .add(new Pair<>(R.color.theme_color_dark_orange, R.string.theme_color_dark_orange))
            .add(new Pair<>(R.color.theme_color_brown, R.string.theme_color_brown))
            .add(new Pair<>(R.color.theme_color_gray, R.string.theme_color_gray))
            .add(new Pair<>(R.color.theme_color_blue_gray, R.string.theme_color_blue_gray))
            .list();

    public static void selectThemeColor(Context context) {
        List<ColorSelectActivity.ColorItem> colorItems = new ArrayList<>(COLOR_ITEMS.size());
        for (Pair<Integer, Integer> item : COLOR_ITEMS) {
            colorItems.add(new ColorSelectActivity.ColorItem(context.getString(item.second),
                    context.getResources().getColor(item.first)));
        }
        ColorSelectActivity.startColorSelect(context, context.getString(R.string.mt_color_picker_title), colorItems);
    }

    @AfterViews
    void setUpUI() {
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


    public static class PreferenceFragment extends ThemeColorPreferenceFragment {

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
                            selectThemeColor(getActivity());
                        }
                    })
                    .entry(getString(R.string.text_reset_background), new Runnable() {
                        @Override
                        public void run() {
                            // EventBus.getDefault().post(new MessageEvent(MainActivity.MESSAGE_CLEAR_BACKGROUND_SETTINGS));
                            Toast.makeText(getActivity(), R.string.text_already_reset, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .entry(getString(R.string.show_ad), new Runnable() {
                        @Override
                        public void run() {
                            SplashActivity_.intent(getActivity())
                                    .extra(SplashActivity.NOT_START_MAIN_ACTIVITY, true)
                                    .extra(SplashActivity.FORCE_SHOW_AD, true)
                                    .start();
                        }
                    })
                    .entry(getString(R.string.text_check_for_updates), new Runnable() {
                        @Override
                        public void run() {
                            new UpdateCheckDialog(getActivity())
                                    .show();
                        }
                    })
                    .entry(getString(R.string.text_issue_report), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), IssueReporterActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    })
                    .entry(getString(R.string.text_join_qq_group), new Runnable() {
                        @Override
                        public void run() {
                            if (!IntentUtil.joinQQGroup(getActivity(), "-7riBQuwFUUqdgYL5vFeIdBfH4H9m-Uj")) {
                                Toast.makeText(getActivity(), R.string.text_mobile_qq_not_installed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .entry(getString(R.string.text_about_me_and_repo), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), AboutActivity_.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
