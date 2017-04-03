package com.stardust.autojs.script;

/**
 * Created by Stardust on 2017/4/2.
 */

public class MultiScriptSource extends ScriptSource {

    private String mScript;
    private FileScriptSource mFileScriptSource;


    public MultiScriptSource(StringScriptSource stringScriptSource, FileScriptSource fileScriptSource) {
        super(fileScriptSource.getName());
        mFileScriptSource = fileScriptSource;
        StringBuilder stringBuilder = new StringBuilder();
        if (stringScriptSource.getScript() != null)
            stringBuilder.append(stringScriptSource.getScript()).append("\n");
        stringBuilder.append(fileScriptSource.getScript());
        mScript = stringBuilder.toString();
    }

    @Override
    public String getScript() {
        return mScript;
    }

    @Override
    public String toString() {
        return mFileScriptSource.toString();
    }

}
