package com.stardust.autojs.execution;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.stardust.autojs.core.eventloop.EventEmitter;
import com.stardust.autojs.core.eventloop.SimpleEvent;
import com.stardust.autojs.core.ui.inflater.inflaters.Exceptions;
import com.stardust.autojs.engine.JavaScriptEngine;
import com.stardust.autojs.engine.LoopBasedJavaScriptEngine;
import com.stardust.autojs.engine.ScriptEngine;
import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.api.UI;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.util.IntentExtras;

import org.mozilla.javascript.NativeObject;

/**
 * Created by Stardust on 2017/2/5.
 */

public class ScriptExecuteActivity extends AppCompatActivity {


    private static final String EXTRA_EXECUTION = ScriptExecuteActivity.class.getName() + ".execution";
    private Object mResult;
    private ScriptEngine mScriptEngine;
    private ScriptExecutionListener mExecutionListener;
    private ScriptSource mScriptSource;
    private ActivityScriptExecution mScriptExecution;
    private IntentExtras mIntentExtras;
    private ScriptRuntime mRuntime;


    private EventEmitter mEventEmitter;

    public static ActivityScriptExecution execute(Context context, ScriptEngineManager manager, ScriptExecutionTask task) {
        ActivityScriptExecution execution = new ActivityScriptExecution(manager, task);
        Intent i = new Intent(context, ScriptExecuteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentExtras.newExtras()
                .put(EXTRA_EXECUTION, execution)
                .putInIntent(i);
        context.startActivity(i);
        return execution;
    }

    // FIXME: 2018/3/16 如果Activity被回收则得不到改进
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentExtras = readIntentExtras(savedInstanceState);
        if (mIntentExtras == null || mIntentExtras.get(EXTRA_EXECUTION) == null) {
            finish();
            return;
        }
        mScriptExecution = mIntentExtras.get(EXTRA_EXECUTION);
        mScriptSource = mScriptExecution.getSource();
        mScriptEngine = mScriptExecution.createEngine(this);
        mExecutionListener = mScriptExecution.getListener();
        mRuntime = ((JavaScriptEngine) mScriptEngine).getRuntime();
        mEventEmitter = new EventEmitter(mRuntime.bridges);
        runScript();
    }

    public EventEmitter getEventEmitter() {
        return mEventEmitter;
    }

    private IntentExtras readIntentExtras(Bundle savedInstanceState) {
        IntentExtras extras = IntentExtras.fromIntentAndRelease(getIntent());
        if (extras == null && savedInstanceState != null) {
            int id = savedInstanceState.getInt(IntentExtras.EXTRA_ID, -1);
            if (id == -1) {
                return null;
            }
            extras = IntentExtras.fromIdAndRelease(id);
        }
        return extras;
    }

    private void runScript() {
        try {
            prepare();
            doExecution();
        } catch (Exception e) {
            onException(e);
        }
    }

    private void onException(Exception e) {
        mExecutionListener.onException(mScriptExecution, e);
        super.finish();
    }

    @SuppressWarnings("unchecked")
    private void doExecution() {
        mScriptEngine.setTag(ScriptEngine.TAG_SOURCE, mScriptSource);
        mExecutionListener.onStart(mScriptExecution);
        ((LoopBasedJavaScriptEngine) mScriptEngine).execute(mScriptSource, new LoopBasedJavaScriptEngine.ExecuteCallback() {
            @Override
            public void onResult(Object r) {
                mResult = r;
            }

            @Override
            public void onException(Exception e) {
                ScriptExecuteActivity.this.onException(e);
            }
        });
    }

    private void prepare() {
        mScriptEngine.put("activity", this);
        mScriptEngine.setTag("activity", this);
        mScriptEngine.setTag(ScriptEngine.TAG_ENV_PATH, mScriptExecution.getConfig().getRequirePath());
        mScriptEngine.setTag(ScriptEngine.TAG_EXECUTE_PATH, mScriptExecution.getConfig().getExecutePath());
        mScriptEngine.init();
    }

    @Override
    public void finish() {
        Exception exception = mScriptEngine.getUncaughtException();
        if (exception != null) {
            onException(exception);
        } else {
            mExecutionListener.onSuccess(mScriptExecution, mResult);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScriptEngine.put("activity", null);
        mScriptEngine.setTag("activity", null);
        mScriptEngine.destroy();
        mScriptExecution = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mIntentExtras == null)
            return;
        IntentExtras extras = IntentExtras.newExtras().putAll(mIntentExtras);
        outState.putInt(IntentExtras.EXTRA_ID, extras.getId());
    }

    @Override
    public void onBackPressed() {
        SimpleEvent event = new SimpleEvent();
        emit("back_pressed", event);
        if (!event.consumed) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        emit("pause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        emit("resume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        emit("restore_instance_state", savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        emit("save_instance_state", outState, outPersistentState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SimpleEvent e = new SimpleEvent();
        emit("key_down", keyCode, event, e);
        return e.consumed || super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        SimpleEvent e = new SimpleEvent();
        emit("generic_motion_event", event, e);
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        emit("activity_result", requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        emit("create_options_menu", menu);
        return menu.size() > 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SimpleEvent e = new SimpleEvent();
        emit("options_item_selected", e, item);
        return e.consumed || super.onOptionsItemSelected(item);
    }

    public void emit(String event, Object... args) {
        try {
            mEventEmitter.emit(event, (Object[]) args);
        } catch (Exception e) {
            mRuntime.exit(e);
        }
    }

    public void setSupportActionBar(@Nullable NativeObject toolbar) {
        super.setSupportActionBar(UI.unwrapJsViewObject(toolbar, Toolbar.class));
    }

    private static class ActivityScriptExecution extends ScriptExecution.AbstractScriptExecution {

        private ScriptEngine mScriptEngine;
        private ScriptEngineManager mScriptEngineManager;

        ActivityScriptExecution(ScriptEngineManager manager, ScriptExecutionTask task) {
            super(task);
            mScriptEngineManager = manager;
        }

        @SuppressWarnings("unchecked")
        public ScriptEngine createEngine(Activity activity) {
            if (mScriptEngine != null) {
                mScriptEngine.forceStop();
            }
            mScriptEngine = mScriptEngineManager.createEngineOfSourceOrThrow(getSource());
            return mScriptEngine;
        }

        @Override
        public ScriptEngine getEngine() {
            return mScriptEngine;
        }

    }


}
