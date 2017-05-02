package com.stardust.automator;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/3/9.
 */

public class AccessibilityEventCommandHost implements AccessibilityDelegate {


    public interface Command {

        void execute(AccessibilityService service, AccessibilityEvent event);

        boolean isValid();

        void setValid(boolean valid);
    }

    public abstract static class AbstractCommand implements Command {

        private boolean mValid;

        public synchronized boolean isValid() {
            return mValid;
        }

        public synchronized void setValid(boolean valid) {
            mValid = valid;
        }

    }

    private static final String TAG = "CommandHostDelegate";

    public static final int RUN_MODE_SINGLE_THREAD = 0;
    public static final int RUN_MODE_THREAD_POOL = 1;
    public static final int RUN_MODE_NEW_THREAD_EVERY_TIME = 2;


    private final Queue<Command> mCommands = new LinkedList<>();
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private int mRunMode = RUN_MODE_THREAD_POOL;

    @Override
    public boolean onAccessibilityEvent(final AccessibilityService service, final AccessibilityEvent event) {
        synchronized (mCommands) {
            if (!mCommands.isEmpty()) {
                Log.v(TAG, "will execute " + mCommands.size() + " commands");
            }
            while (!mCommands.isEmpty()) {
                final Command command = mCommands.poll();
                if (command.isValid()) {
                    executeCommand(command, service, event);
                }
            }
        }
        return false;
    }

    @Override
    public Set<Integer> getEventTypes() {
        return null;
    }

    private void executeCommand(final Command command, final AccessibilityService service, final AccessibilityEvent event) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (!command.isValid()) {
                    return;
                }
                Log.v(TAG, "executing " + command);
                command.execute(service, event);
                synchronized (command) {
                    command.notify();
                }
            }
        };
        if (mRunMode == RUN_MODE_SINGLE_THREAD) {
            r.run();
        } else if (mRunMode == RUN_MODE_NEW_THREAD_EVERY_TIME) {
            new Thread(r).start();
        } else {
            mExecutor.execute(r);
        }
    }


    public void executeAndWaitForEvent(Command command) {
        synchronized (mCommands) {
            mCommands.offer(command);
        }
        synchronized (command) {
            try {
                command.wait();
            } catch (InterruptedException e) {
                command.setValid(false);
                throw new RuntimeException(e);
            }
        }
    }

    public void setRunMode(int mode) {
        mRunMode = mode;
    }

}
