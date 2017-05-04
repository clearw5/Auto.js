package com.stardust.scriptdroid.script;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.App;

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

    private static ScriptFileList instance = new SharedPrefScriptFileList(App.getApp());

    private SharedPreferences mSharedPreferences;
    private List<String> mScriptPath;
    private List<String> mScriptName;

    public static ScriptFileList getInstance() {
        return instance;
    }

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
        mScriptName.add(scriptFile.getSimplifiedName());
        mScriptPath.add(scriptFile.getSimplifiedPath());
        syncWithSharedPref();
    }

    @Override
    public ScriptFile get(int i) {
        return new ScriptFile(mScriptPath.get(i));
    }

    @Override
    public void remove(int i) {
        mScriptName.remove(i);
        mScriptPath.remove(i);
        syncWithSharedPref();
    }

    @Override
    public void rename(int position, String newName, boolean renameFile) {
        mScriptName.set(position, newName);
        if (renameFile) {
            String newPath = PFile.renameWithoutExtension(mScriptPath.get(position), newName);
            mScriptPath.set(position, newPath);
        }
        syncWithSharedPref();
    }

    @Override
    public int size() {
        return mScriptPath.size();
    }

    @Override
    public boolean containsPath(String path) {
        return mScriptPath.contains(path);
    }

    protected void syncWithSharedPref() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SP_KEY_SCRIPT_NAME, GSON.toJson(mScriptName));
        editor.putString(SP_KEY_SCRIPT_PATH, GSON.toJson(mScriptPath));
        editor.apply();
    }
}
