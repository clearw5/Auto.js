package com.stardust.autojs.engine;

import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface JavaScriptEngine {

    void put(String name, Object value);

    Object execute(ScriptSource scriptSource);

    void stop();

    void stopNotRemoveFromManager();

    void destroy();

    void init();
}
