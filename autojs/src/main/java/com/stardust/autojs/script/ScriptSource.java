package com.stardust.autojs.script;

import java.io.Serializable;

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
