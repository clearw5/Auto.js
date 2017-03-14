package com.stardust.scriptdroid.sample;

import android.content.Context;
import android.content.res.AssetManager;

import com.stardust.scriptdroid.Pref;
import com.stardust.scriptdroid.droid.script.file.ScriptFile;
import com.stardust.scriptdroid.droid.script.file.ScriptFileList;
import com.stardust.scriptdroid.droid.script.file.SharedPrefScriptFileList;
import com.stardust.scriptdroid.tool.FileUtils;
import com.stardust.util.MapEntries;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/1/30.
 */
public class SampleFileManager {

    private static final Map<String, Integer> SAMPLES = new MapEntries<>(new LinkedHashMap<String, Integer>())
            .entry("sample/自动操作/sample_open_running_services.js", R.string.text_sample_open_running_services)
            .entry("sample/自动操作/sample_qq_hongbao.js", R.string.text_sample_qq_hongbao)
            .entry("sample/自动操作/sample_simple_calculator.js", R.string.text_sample_simple_calculator)
            .entry("sample/自动操作/sample_force_qq_chat.js", R.string.text_sample_for_qq_chat)
            .entry("sample/自动操作/sample_wechat_scan.js", R.string.text_sample_wechat_scan)
            .entry("sample/自动操作/sample_alipay_scan.js", R.string.text_sample_alipay_scan)
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

    public List<SampleGroup> getSamplesFromAssets(AssetManager assets, String path) {
        List<SampleGroup> sampleGroups = new ArrayList<>();
        try {
            String[] groups = assets.list(path);
            for (String groupName : groups) {
                sampleGroups.add(getSampleGroupFromAssets(assets, groupName, path + "/" + groupName));
            }
            return sampleGroups;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SampleGroup getSampleGroupFromAssets(AssetManager assets, String groupName, String path) {
        SampleGroup group = new SampleGroup(groupName);
        try {
            String[] samples = assets.list(path);
            for (String sample : samples) {
                group.add(new Sample(FileUtils.getNameWithoutExtension(sample), path + "/" + sample));
            }
            return group;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
