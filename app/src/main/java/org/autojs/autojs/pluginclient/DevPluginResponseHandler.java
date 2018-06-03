package org.autojs.autojs.pluginclient;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFiles;

import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.storage.file.StorageFileProvider;

import java.util.HashMap;

/**
 * Created by Stardust on 2017/5/11.
 */

public class DevPluginResponseHandler implements Handler {


    private Router mRouter = new Router("type")
            .handler("command", new Router("command")
                    .handler("run", data -> {
                        String script = data.get("script").getAsString();
                        String name = getName(data);
                        String viewId = data.get("view_id").getAsString();
                        runScript(viewId, name, script);
                        return false;
                    })
                    .handler("stop", data -> {
                        String viewId = data.get("view_id").getAsString();
                        stopScript(viewId);
                        return true;
                    })
                    .handler("save", data -> {
                        String script = data.get("script").getAsString();
                        String name = getName(data);
                        saveScript(name, script);
                        return false;
                    })
                    .handler("rerun", data -> {
                        String viewId = data.get("view_id").getAsString();
                        String script = data.get("script").getAsString();
                        String name = getName(data);
                        stopScript(viewId);
                        runScript(viewId, name, script);
                        return false;
                    })
                    .handler("stopAll", data -> {
                        AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
                        return false;
                    }));


    private HashMap<String, ScriptExecution> mScriptExecutions = new HashMap<>();

    @Override
    public boolean handle(JsonObject data) {
        return mRouter.handle(data);
    }

    private void runScript(String viewId, String name, String script) {
        if (TextUtils.isEmpty(name)) {
            name = "[" + viewId + "]";
        } else {
            name = PFiles.getNameWithoutExtension(name);
        }
        mScriptExecutions.put(viewId, Scripts.run(new StringScriptSource("[remote]" + name, script)));
    }

    private void stopScript(String viewId) {
        ScriptExecution execution = mScriptExecutions.get(viewId);
        if (execution != null) {
            execution.getEngine().forceStop();
            mScriptExecutions.remove(viewId);
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
        name = PFiles.getNameWithoutExtension(name);
        if (!name.endsWith(".js")) {
            name = name + ".js";
        }
        PFiles.write(StorageFileProvider.getDefaultDirectoryPath() + name, script);
        GlobalAppContext.toast(R.string.text_script_save_successfully);
    }
}
