package com.stardust.scriptdroid.statics;

import com.stardust.autojs.script.ScriptSource;

import java.util.Map;

/**
 * Created by Stardust on 2017/5/5.
 */

public interface ScriptStaticsStorage {

    void record(ScriptSource source);

    Map<String, String> getAll();

    Map<String, String> getMax(int size);

    void clear();

    void close();
}
