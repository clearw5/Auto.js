package org.autojs.autojs.external.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.stardust.app.GlobalAppContext;
import org.autojs.autojs.App;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/7/11.
 */

public class ScriptWidgets {


    private static final String LOG_TAG = "ScriptWidgets";
    private static SharedPreferences widgets = GlobalAppContext.get().getSharedPreferences("ScriptWidgets", Context.MODE_PRIVATE);
    private static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z]+_([0-9])+");

    static String getPathForAppWidgetId(int id) {
        return widgets.getString("path_" + id, null);
    }

    static void setPathForAppWidgetId(int id, String path) {
        widgets.edit().putString("path_" + id, path).apply();
    }

    static int getRequestCodeForAppWidgetId(int id) {
        int requestCode = widgets.getInt("rc_" + id, -1);
        if (requestCode == -1) {
            requestCode = widgets.getInt("max_rc", 0) + 1;
            widgets.edit()
                    .putInt("rc_" + id, requestCode)
                    .putInt("max_rc", requestCode)
                    .apply();
        }
        return requestCode;
    }

    static void removeAllNotIn(Set<Integer> appWidgetIdSet) {
        List<String> keysToRemove = new LinkedList<>();
        for (Map.Entry<String, ?> entry : widgets.getAll().entrySet()) {
            if (entry.getKey().equals("max_rc")) {
                continue;
            }
            Matcher matcher = ID_PATTERN.matcher(entry.getKey());
            if (matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));
                if (!appWidgetIdSet.contains(id)) {
                    keysToRemove.add(entry.getKey());
                }
            } else {
                Log.w(LOG_TAG, "illegal key: " + entry.getKey());
                keysToRemove.add(entry.getKey());
            }
        }
        SharedPreferences.Editor editor = widgets.edit();
        for (String key : keysToRemove) {
            editor.remove(key);
            Log.v(LOG_TAG, "remove key: " + key);
        }
        editor.apply();
    }
}
