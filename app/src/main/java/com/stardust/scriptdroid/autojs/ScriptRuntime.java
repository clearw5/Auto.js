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


    public static class Builder {
        private AppUtils mAppUtils;
        private UiHandler mUiHandler;
        private Console mConsole;
        private AccessibilityBridge mAccessibilityBridge;
        private Supplier<AbstractShell> mShellSupplier;


        public Builder() {

        }

        public Builder setAppUtils(AppUtils appUtils) {
            mAppUtils = appUtils;
            return this;
        }

        public Builder setUiHandler(UiHandler uiHandler) {
            mUiHandler = uiHandler;
            return this;
        }

        public Builder setConsole(Console console) {
            mConsole = console;
            return this;
        }

        public Builder setAccessibilityBridge(AccessibilityBridge accessibilityBridge) {
            mAccessibilityBridge = accessibilityBridge;
            return this;
        }

        public Builder setShellSupplier(Supplier<AbstractShell> shellSupplier) {
            mShellSupplier = shellSupplier;
            return this;
        }

        public ScriptRuntime build() {
            return new ScriptRuntime(mAppUtils, mUiHandler, mConsole, mAccessibilityBridge, mShellSupplier);
        }

    }

    @ScriptVariable
    public Dialogs dialogs;

    public ScriptRuntime(AppUtils appUtils, UiHandler uiHandler, Console console, AccessibilityBridge accessibilityBridge, Supplier<AbstractShell> shellSupplier) {
        super(appUtils, uiHandler, console, accessibilityBridge, shellSupplier);
        dialogs = new Dialogs(appUtils, uiHandler);
    }


}
