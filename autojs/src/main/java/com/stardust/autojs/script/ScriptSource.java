package com.stardust.autojs.script;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.stardust.util.MapEntries;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/4/2.
 */

public abstract class ScriptSource implements Serializable {

    private String mName;

    public ScriptSource(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public abstract String getEngineName();
}
