package com.stardust.autojs.script;


/**
 * Created by Stardust on 2017/4/2.
 */

public class StringScriptSource extends ScriptSource {

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

}
