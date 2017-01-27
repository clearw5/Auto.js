package com.stardust.scriptdroid.droid;

import android.content.Context;

import com.stardust.scriptdroid.action.ActionPerformService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Stardust on 2017/1/23.
 */

public class Droid {

    public static void run(Context context, File file) {
        Interpreter interpreter = new Interpreter(context);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            String str = new String(bytes);
            ActionPerformService.setActions(interpreter.interpreterAll(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
