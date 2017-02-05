package com.stardust.scriptdroid;

import android.content.Context;

import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.file.FileUtils;
import com.stardust.util.MapEntries;

import java.util.Map;

/**
 * Created by Stardust on 2017/1/30.
 */
public class NonUiInitializer {

    private static final Map<String, Integer> SAMPLES = new MapEntries<String, Integer>()
            .entry("sample_open_wechat_moment.js", R.string.text_sample_open_what_moment)
            .entry("sample_open_running_services.js", R.string.text_sample_open_running_services)
            .entry("sample_qq_hongbao.js", R.string.text_sample_qq_hongbao)
            .entry("sample_simple_calculator.js", R.string.text_sample_simple_calculator)
            .entry("sample_force_qq_chat.js", R.string.text_sample_for_qq_chat)
            .map();

    private static NonUiInitializer instance = new NonUiInitializer();

    public static NonUiInitializer getInstance() {
        return instance;
    }

    private Context getContext() {
        return App.getApp();
    }

    public void init() {

    }

    public void copySampleScriptFileIfNeeded() {
        if (!Pref.def().getBoolean(Pref.SAMPLE_SCRIPTS_COPIED, false)) {
            copySampleScriptFile();
        }
    }

    public int copySampleScriptFile() {
        ScriptFileList list = SharedPrefScriptFileList.getInstance();
        int failCount = 0;
        for (Map.Entry<String, Integer> entry : SAMPLES.entrySet()) {
            String assetFile = entry.getKey();
            String name = getContext().getString(entry.getValue());
            String path = ScriptFile.DEFAULT_FOLDER + name + ".js";
            if (!list.containsPath(path)) {
                if (FileUtils.copyAsset(assetFile, path)) {
                    list.add(new ScriptFile(name, path));
                } else {
                    failCount++;
                }
            }
        }
        if (failCount > 0) {
            Pref.def().edit().putBoolean(Pref.SAMPLE_SCRIPTS_COPIED, true).apply();
        }
        return failCount;
    }

}
