package com.stardust.scriptdroid.sublime;

import android.text.TextUtils;
import android.util.SparseArray;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFile;
import com.stardust.scriptdroid.App;
import com.stardust.scriptdroid.R;
import com.stardust.scriptdroid.autojs.AutoJs;
import com.stardust.scriptdroid.script.Scripts;
import com.stardust.scriptdroid.script.StorageFileProvider;

/**
 * Created by Stardust on 2017/5/11.
 */

public class SublimeResponseHandler implements Handler {


    private Router mRouter = new Router("type")
            .handler("command", new Router("command")
                    .handler("run", new Handler() {
                        @Override
                        public boolean handle(JsonObject data) {
                            String script = data.get("script").getAsString();
                            String name = getName(data);
                            int viewId = data.get("view_id").getAsInt();
                            runScript(viewId, name, script);
                            return false;
                        }
                    })
                    .handler("stop", new Handler() {
                        @Override
                        public boolean handle(JsonObject data) {
                            int viewId = data.get("view_id").getAsInt();
                            stopScript(viewId);
                            return true;
                        }
                    })
                    .handler("save", new Handler() {
                        @Override
                        public boolean handle(JsonObject data) {
                            String script = data.get("script").getAsString();
                            String name = getName(data);
                            saveScript(name, script);
                            return false;
                        }
                    })
                    .handler("rerun", new Handler() {

                        @Override
                        public boolean handle(JsonObject data) {
                            int viewId = data.get("view_id").getAsInt();
                            String script = data.get("script").getAsString();
                            String name = getName(data);
                            stopScript(viewId);
                            runScript(viewId, name, script);
                            return false;
                        }
                    })
                    .handler("stopAll", new Handler() {
                        @Override
                        public boolean handle(JsonObject data) {
                            AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
                            return false;
                        }
                    }));


    private SparseArray<ScriptExecution> mScriptExecutions = new SparseArray<>();

    @Override
    public boolean handle(JsonObject data) {
        return mRouter.handle(data);
    }

    private void runScript(int viewId, String name, String script) {
        if (TextUtils.isEmpty(name)) {
            name = "[" + viewId + "]";
        } else {
            name = PFile.getNameWithoutExtension(name);
        }
        mScriptExecutions.put(viewId, Scripts.run(new StringScriptSource("<remote>:" + name, script)));
    }

    private void stopScript(int viewId) {
        ScriptExecution execution = mScriptExecutions.get(viewId);
        if (execution != null) {
            execution.getEngine().forceStop();
            mScriptExecutions.delete(viewId);
        }
    }

    private String getName(JsonObject data) {
        JsonElement element = data.get("name");
        if (element instanceof JsonNull) {
            return null;
        }
        return element.getAsString();
    }

    private void saveScript(String name, String script) {
        if (TextUtils.isEmpty(name)) {
            name = "untitled";
        }
        name = PFile.getNameWithoutExtension(name);
        if (!name.endsWith(".js")) {
            name = "<remote>" + name + ".js";
        } else {
            name = "<remote>" + name;
        }
        PFile.write(StorageFileProvider.DEFAULT_DIRECTORY_PATH + name, script);
        App.getApp().getUiHandler().toast(R.string.text_script_save_successfully);
    }
}
