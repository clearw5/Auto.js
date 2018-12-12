package com.stardust.autojs.script;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.rhino.TokenStream;
import com.stardust.util.MapBuilder;

import org.mozilla.javascript.Token;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * Created by Stardust on 2017/8/2.
 */

public abstract class JavaScriptSource extends ScriptSource {

    public static final String ENGINE = "com.stardust.autojs.script.JavaScriptSource.Engine";

    public static final String EXECUTION_MODE_UI_PREFIX = "\"ui\";";

    public static final int EXECUTION_MODE_NORMAL = 0;
    public static final int EXECUTION_MODE_UI = 0x00000001;
    public static final int EXECUTION_MODE_AUTO = 0x00000002;

    private static final String LOG_TAG = "JavaScriptSource";

    private static final Map<String, Integer> EXECUTION_MODES = new MapBuilder<String, Integer>()
            .put("ui", EXECUTION_MODE_UI)
            .put("auto", EXECUTION_MODE_AUTO)
            .build();
    private static final int PARSING_MAX_TOKEN = 300;

    private int mExecutionMode = -1;

    public JavaScriptSource(String name) {
        super(name);
    }

    @NonNull
    public abstract String getScript();

    @Nullable
    public abstract Reader getScriptReader();

    @NonNull
    public Reader getNonNullScriptReader() {
        Reader reader = getScriptReader();
        if (reader == null) {
            return new StringReader(getScript());
        }
        return reader;
    }

    public String toString() {
        return getName() + ".js";
    }


    public int getExecutionMode() {
        if (mExecutionMode == -1) {
            mExecutionMode = parseExecutionMode();
        }
        return mExecutionMode;
    }

    protected int parseExecutionMode() {
        String script = getScript();
        TokenStream ts = new TokenStream(new StringReader(script), null, 1);
        int token;
        int count = 0;
        try {
            while (count <= PARSING_MAX_TOKEN && (token = ts.getToken()) != Token.EOF) {
                count++;
                if (token == Token.EOL || token == Token.COMMENT) {
                    continue;
                }
                if (token == Token.STRING && ts.getTokenLength() > 2) {
                    String tokenString = script.substring(ts.getTokenBeg() + 1, ts.getTokenEnd() - 1);
                    if (ts.getToken() != Token.SEMI) {
                        break;
                    }
                    Log.d(LOG_TAG, "string = " + tokenString);
                    return parseExecutionMode(tokenString.split(" "));
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return EXECUTION_MODE_NORMAL;
        }
        return EXECUTION_MODE_NORMAL;

    }

    private int parseExecutionMode(String[] modeStrings) {
        int mode = 0;
        for (String modeString : modeStrings) {
            Integer i = EXECUTION_MODES.get(modeString);
            if (i != null) {
                mode |= i;
            }
        }
        return mode;
    }

    @Override
    public String getEngineName() {
        return ENGINE;
    }


}
