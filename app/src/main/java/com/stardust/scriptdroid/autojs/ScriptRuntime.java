package com.stardust.scriptdroid.autojs;

import com.stardust.autojs.runtime.AccessibilityBridge;
import com.stardust.autojs.runtime.ScriptVariable;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.scriptdroid.autojs.api.Dialogs;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2017/5/8.
 */

public class ScriptRuntime extends com.stardust.autojs.runtime.ScriptRuntime {


    public static class Builder extends com.stardust.autojs.runtime.ScriptRuntime.Builder {


        public Builder() {

        }


        public ScriptRuntime build() {
            return new ScriptRuntime(this);
        }

    }

    @ScriptVariable
    public Dialogs dialogs;

    private ScriptRuntime(Builder builder) {
        super(builder);
        dialogs = new Dialogs(app, getUiHandler());
    }


}
