package com.stardust.scriptdroid;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stardust.util.MapEntries;

import java.util.Map;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * Created by Stardust on 2017/2/2.
 */

public class SettingsActivity extends BaseActivity {

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
                    .entry(getString(R.string.text_about), new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), AboutActivity.class));
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
            new LicensesDialog.Builder(getActivity())
                    .setNotices(R.raw.licenses)
                    .setIncludeOwnLicense(true)
                    .build()
                    .showAppCompat();
        }

    }
}
