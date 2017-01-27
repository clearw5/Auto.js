package com.stardust.scriptdroid.droid.script.file;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/23.
 */

public class SharedPrefScriptFileList extends ScriptFileList {

    private static final Gson GSON = new Gson();

    private static final String SP_KEY_SCRIPT_NAME = "script_name";
    private static final String SP_KEY_SCRIPT_PATH = "script_path";

    private SharedPreferences mSharedPreferences;
    private List<String> mScriptPath;
    private List<String> mScriptName;


    public SharedPrefScriptFileList(Context context) {
        mSharedPreferences = context.getSharedPreferences("SharedPrefScriptFileList", Context.MODE_PRIVATE);
        readFromSharedPref();
    }

    private void readFromSharedPref() {
        Type type = new TypeToken<List<String>>() {
        }.getType();
        mScriptName = GSON.fromJson(mSharedPreferences.getString(SP_KEY_SCRIPT_NAME, ""), type);
        mScriptPath = GSON.fromJson(mSharedPreferences.getString(SP_KEY_SCRIPT_PATH, ""), type);
        if (mScriptName == null || mScriptPath == null || mScriptName.size() != mScriptPath.size()) {
            reset();
        }
    }

    private void reset() {
        mScriptName = new ArrayList<>();
        mScriptPath = new ArrayList<>();
        syncWithSharedPref();
    }

    @Override
    public void add(ScriptFile scriptFile) {
        mScriptName.add(scriptFile.name);
        mScriptPath.add(scriptFile.path);
        syncWithSharedPref();
    }

    @Override
    public ScriptFile get(int i) {
        return new ScriptFile(mScriptName.get(i), mScriptPath.get(i));
    }

    @Override
    public void remove(int i) {
        mScriptName.remove(i);
        mScriptPath.remove(i);
        syncWithSharedPref();
    }

    @Override
    public void rename(int position, String newName) {
        mScriptName.set(position, newName);
        syncWithSharedPref();
    }

    @Override
    public int size() {
        return mScriptPath.size();
    }

    protected void syncWithSharedPref() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SP_KEY_SCRIPT_NAME, GSON.toJson(mScriptName));
        editor.putString(SP_KEY_SCRIPT_PATH, GSON.toJson(mScriptPath));
        editor.apply();
    }
}
