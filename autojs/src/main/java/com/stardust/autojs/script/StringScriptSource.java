package com.stardust.autojs.script;


import androidx.annotation.Nullable;

import java.io.Reader;

/**
 * Created by Stardust on 2017/4/2.
 */

public class StringScriptSource extends JavaScriptSource {

    private String mScript;

    public StringScriptSource(String script) {
        super("Tmp");
        mScript = script;
    }

    public StringScriptSource(String name, String script) {
        super(name);
        mScript = script;
    }

    @Override
    public String getScript() {
        return mScript;
    }

    @Nullable
    @Override
    public Reader getScriptReader() {
        return null;
    }

}
