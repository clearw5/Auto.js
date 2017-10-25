package com.stardust.autojs.script;

import android.content.Context;
import android.util.Log;

import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/4/12.
 */

public class JsBeautifier {

    public interface Callback {

        void onSuccess(String beautifiedCode);

        void onException(Exception e);
    }

    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Context mContext;
    private Function mJsBeautifyFunction;
    private org.mozilla.javascript.Context mScriptContext;
    private Scriptable mScriptable;
    private final String mBeautifyJsPath;

    public JsBeautifier(Context context, String beautifyJsPath) {
        mContext = context;
        mBeautifyJsPath = beautifyJsPath;
    }

    public void beautify(final String code, final Callback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    prepareIfNeeded();
                    enterContext();
                    Object beautifiedCode = mJsBeautifyFunction.call(mScriptContext, mScriptable, mScriptable, new Object[]{code});
                    Object o = mScriptContext.evaluateString(mScriptable, " (<xml id=\"foo\"></xml>).attributes()[0].name()", "<e4x>", 1, null);
                    Log.i("e4x", o + "");
                    callback.onSuccess(beautifiedCode.toString());
                } catch (Exception e) {
                    callback.onException(e);
                } finally {
                    exitContext();
                }
            }

        });
    }

    private void exitContext() {
        if (mScriptContext != null) {
            org.mozilla.javascript.Context.exit();
            mScriptContext = null;
        }
    }

    private void enterContext() {
        if (mScriptContext == null) {
            mScriptContext = org.mozilla.javascript.Context.enter();
            mScriptContext.setLanguageVersion(org.mozilla.javascript.Context.VERSION_1_8);
            mScriptContext.setOptimizationLevel(-1);
        }
    }

    public void prepare() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    prepareIfNeeded();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


    private void prepareIfNeeded() {
        if (mJsBeautifyFunction != null)
            return;
        compile();
    }

    private void compile() {
        try {
            enterContext();
            InputStream is = mContext.getAssets().open(mBeautifyJsPath);
            if (mScriptable == null)
                mScriptable = mScriptContext.initSafeStandardObjects();
            mJsBeautifyFunction = mScriptContext.compileFunction(mScriptable, PFiles.read(is), "<js_beautify>", 1, null);
        } catch (IOException e) {
            exitContext();
            throw new UncheckedIOException(e);
        }
    }

}
