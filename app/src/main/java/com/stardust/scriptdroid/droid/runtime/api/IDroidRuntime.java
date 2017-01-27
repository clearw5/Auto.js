package com.stardust.scriptdroid.droid.runtime.api;

import com.stardust.scriptdroid.droid.runtime.action.ActionTarget;

/**
 * Created by Stardust on 2017/1/27.
 */
public interface IDroidRuntime {

    void launchPackage(String packageName);

    void launch(String packageName, String className);

    void launchApp(String appName);

    String[] getPackageName(String appName);

    ActionTarget text(String text);

    ActionTarget bounds(int left, int top, int right, int bottom);

    ActionTarget editable(int i);

    boolean click(ActionTarget target);

    boolean longClick(ActionTarget target);

    boolean scrollUp(ActionTarget target);

    boolean scrollDown(ActionTarget target);

    boolean focus(ActionTarget target);

    boolean select(ActionTarget target);

    boolean setText(ActionTarget target, String text);

    boolean paste(ActionTarget target);

    void toast(String text);

    void sleep(long millis);

}
