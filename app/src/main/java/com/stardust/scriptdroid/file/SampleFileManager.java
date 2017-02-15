package com.stardust.scriptdroid.file;

import android.content.Context;

import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.util.MapEntries;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/1/30.
 */
public class SampleFileManager {

    private static final Map<String, Integer> SAMPLES = new MapEntries<>(new LinkedHashMap<String, Integer>())
            .entry("sample_open_running_services.js", R.string.text_sample_open_running_services)
            .entry("sample_qq_hongbao.js", R.string.text_sample_qq_hongbao)
            .entry("sample_simple_calculator.js", R.string.text_sample_simple_calculator)
            .entry("sample_force_qq_chat.js", R.string.text_sample_for_qq_chat)
            .entry("sample_wechat_scan.js", R.string.text_sample_wechat_scan)
            .entry("sample_alipay_scan.js", R.string.text_sample_alipay_scan)
            .map();

    private static SampleFileManager instance = new SampleFileManager();

    public static SampleFileManager getInstance() {
        return instance;
    }

    private Context getContext() {
        return App.getApp();
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
        if (failCount < SAMPLES.size()) {
            Pref.def().edit().putBoolean(Pref.SAMPLE_SCRIPTS_COPIED, true).apply();
        }
        return failCount;
    }

}
