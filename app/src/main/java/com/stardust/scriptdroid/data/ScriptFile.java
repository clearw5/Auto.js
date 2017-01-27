package com.stardust.scriptdroid.data;

import android.content.Context;
import android.os.Environment;

import com.stardust.scriptdroid.action.Action;
import com.stardust.scriptdroid.action.ActionPerformService;
import com.stardust.scriptdroid.droid.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptFile {

    public static final String DEFAULT_FOLDER = Environment.getExternalStorageDirectory() + "/脚本/";
    public String name;

    public String path;

    public ScriptFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void run(Context context) {
        Interpreter interpreter = new Interpreter(context);

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<Action> actions = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                actions.add(interpreter.interpreter(line));
                line = reader.readLine();
            }
            ActionPerformService.setActions(actions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File toFile() {
        return new File(path);
    }

}
