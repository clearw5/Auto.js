package com.stardust.auojs.inrt;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


/**
 * Created by Stardust on 2017/12/8.
 */

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(R.id.fragment_setting, new PreferenceFragment()).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.text_settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class PreferenceFragment extends android.preference.PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
}
