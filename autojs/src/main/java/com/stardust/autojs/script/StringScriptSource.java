package com.stardust.autojs.script;


/**
 * Created by Stardust on 2017/4/2.
 */

public class StringScriptSource extends ScriptSource {

    private String mScript;

    public StringScriptSource(String script) {
        mScript = script;
    }

    @Override
    public String getScript() {
        return mScript;
    }
}
