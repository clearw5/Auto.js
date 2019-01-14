package com.stardust.autojs.execution;

import java.io.Serializable;

/**
 * Created by Stardust on 2017/4/2.
 */

public interface ScriptExecutionListener extends Serializable {

    void onStart(ScriptExecution execution);

    void onSuccess(ScriptExecution execution, Object result);

    void onException(ScriptExecution execution, Throwable e);
}
