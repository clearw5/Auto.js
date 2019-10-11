package com.stardust.auojs.inrt

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


/**
 * Created by Stardust on 2017/12/8.
 */

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        setContentView(R.layout.activity_settings)
        fragmentManager.beginTransaction().replace(R.id.fragment_setting, PreferenceFragment()).commit()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.text_settings)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class PreferenceFragment : android.preference.PreferenceFragment() {

        override fun onCreate(@Nullable savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)
        }
    }
}
