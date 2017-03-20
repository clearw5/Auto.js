package com.stardust.automator;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Stardust on 2017/3/9.
 */

public class AccessibilityEventCommandHost implements AccessibilityDelegate {


    public interface Command {

        void execute(AccessibilityService service, AccessibilityEvent event);
    }

    private static final AccessibilityEventCommandHost instance = new AccessibilityEventCommandHost();

    public static AccessibilityEventCommandHost getInstance() {
        return instance;
    }

    private static final String TAG = "CommandHostDelegate";

    private final Queue<Command> mCommands = new LinkedList<>();
    private Executor mExecutor = Executors.newFixedThreadPool(5);

    @Override
    public boolean onAccessibilityEvent(final AccessibilityService service, final AccessibilityEvent event) {
        synchronized (mCommands) {
            if (!mCommands.isEmpty()) {
                Log.v(TAG, "execute " + mCommands.size() + " commands");
            }
            while (!mCommands.isEmpty()) {
                final Command command = mCommands.poll();
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        command.execute(service, event);
                        synchronized (command) {
                            command.notify();
                        }
                    }
                });
            }
        }
        return false;
    }


    public void executeAndWaitForEvent(Command command) {
        synchronized (mCommands) {
            mCommands.offer(command);
        }
        synchronized (command) {
            try {
                command.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
