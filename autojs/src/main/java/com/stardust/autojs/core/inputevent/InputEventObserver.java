package com.stardust.autojs.core.inputevent;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.stardust.autojs.core.record.inputevent.EventFormatException;
import com.stardust.autojs.core.util.Shell;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/8/4.
 */

public class InputEventObserver {

    public static class InputEvent {
        static final Pattern PATTERN = Pattern.compile("^\\[([^\\]]*)\\]\\s+([^:]*):\\s+([^\\s]*)\\s+([^\\s]*)\\s+([^\\s]*)\\s*$");

        static InputEvent parse(String eventStr) {
            Matcher matcher = PATTERN.matcher(eventStr);
            if (!matcher.matches()) {
                throw new EventFormatException(eventStr);
            }
            double time;
            try {
                time = Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                throw new EventFormatException(eventStr, e);
            }
            return new InputEvent(time, matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
        }


        public double time;
        public String device;
        public String type;
        public String code;
        public String value;

        public InputEvent(double time, String device, String type, String code, String value) {
            this.time = time;
            this.device = device;
            this.type = type;
            this.code = code;
            this.value = value;
        }


        @Override
        public String toString() {
            return "Event{" +
                    "time=" + time +
                    ", device='" + device + '\'' +
                    ", type='" + type + '\'' +
                    ", code='" + code + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public interface InputEventListener {
        void onInputEvent(@NonNull InputEvent e);
    }

    private static InputEventObserver sGlobal;
    private CopyOnWriteArrayList<InputEventListener> mInputEventListeners = new CopyOnWriteArrayList<>();
    private Context mContext;
    private Shell mShell;

    public InputEventObserver(Context context) {
        mContext = context;
    }

    public static InputEventObserver getGlobal(Context context) {
        if (sGlobal == null) {
            initGlobal(context);
        }
        return sGlobal;
    }

    private static void initGlobal(Context context) {
        if (sGlobal != null)
            return;
        sGlobal = new InputEventObserver(context);
        sGlobal.observe();
    }

    public void observe() {
        if (mShell != null)
            throw new IllegalStateException("observe() should be called only once");
        mShell = new Shell(mContext, true);
        mShell.setCallback(new Shell.SimpleCallback() {
            @Override
            public void onNewLine(String str) {
                if (mShell.isInitialized()) {
                    onInputEvent(str);
                }
            }

            @Override
            public void onInitialized() {
                mShell.exec("getevent -t");
            }

        });
    }

    public void onInputEvent(String eventStr) {
        if (TextUtils.isEmpty(eventStr) || !eventStr.startsWith("["))
            return;
        try {
            InputEvent event = InputEvent.parse(eventStr);
            dispatchInputEvent(event);
        } catch (Exception ignored) {

        }
    }

    private void dispatchInputEvent(InputEvent event) {
        for (InputEventListener listener : mInputEventListeners) {
            listener.onInputEvent(event);
        }
    }

    public void addListener(InputEventListener listener) {
        mInputEventListeners.add(listener);
    }

    public boolean removeListener(InputEventListener listener) {
        return mInputEventListeners.remove(listener);
    }


    public void recycle() {
        mShell.exit();
    }


}
