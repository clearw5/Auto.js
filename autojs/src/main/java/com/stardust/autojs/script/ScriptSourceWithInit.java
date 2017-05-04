package com.stardust.autojs.script;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptSourceWithInit extends ScriptSource {

    private String mScript;
    private ScriptSource mMainScriptSource;


    public ScriptSourceWithInit(ScriptSource initScriptSource, ScriptSource mainScriptSource) {
        super(mainScriptSource.getName());
        mMainScriptSource = mainScriptSource;
        StringBuilder stringBuilder = new StringBuilder();
        if (initScriptSource.getScript() != null)
            stringBuilder.append(initScriptSource.getScript()).append("\n");
        stringBuilder.append(mainScriptSource.getScript());
        mScript = stringBuilder.toString();
    }

    @Override
    public String getScript() {
        return mScript;
    }

    @Override
    public String toString() {
        return mMainScriptSource.toString();
    }

}
