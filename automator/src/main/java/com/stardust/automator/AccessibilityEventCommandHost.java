package com.stardust.automator;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.view.accessibility.AccessibilityDelegate;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Stardust on 2017/3/9.
 */

public class AccessibilityEventCommandHost implements AccessibilityDelegate {

    public interface Command {

        void execute(AccessibilityService service, AccessibilityEvent event);
    }

    public static final AccessibilityEventCommandHost instance = new AccessibilityEventCommandHost();

    private static final String TAG = "CommandHostDelegate";

    private final Queue<Command> mCommands = new LinkedList<>();

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        synchronized (mCommands) {
            if (!mCommands.isEmpty()) {
                Log.v(TAG, "execute " + mCommands.size() + " commands");
            }
            while (!mCommands.isEmpty()) {
                Command command = mCommands.poll();
                command.execute(service, event);
                synchronized (command) {
                    command.notify();
                }
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
