package com.stardust.autojs.script;

/**
 * Created by Stardust on 2017/4/2.
 */

public class MultiScriptSource extends ScriptSource {

    private String mScript;

    public MultiScriptSource(ScriptSource... sources) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ScriptSource source : sources) {
            String script = source.getScript();
            if (script != null)
                stringBuilder.append(script).append("\n");
        }
        mScript = stringBuilder.toString();
    }

    @Override
    public String getScript() {
        return mScript;
    }
}
