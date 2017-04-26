package com.stardust.autojs.runtime.api;

/**
 * Created by Stardust on 2017/4/24.
 */

public abstract class AbstractShell {

    public AbstractShell() {
        this(false);
    }

    public AbstractShell(boolean root) {
        init(root ? "su" : "sh");
    }

    protected abstract void init(String initialCommand);

    public abstract void exec(String command);

    public void KeyCode(int keyCode) {
        exec("input keyevent " + keyCode);
    }

    public void KeyCode(String keyCode) {
        exec("input keyevent " + keyCode);
    }

    public void Home() {
        KeyCode(3);
    }

    public void Back() {
        KeyCode(4);
    }

    public void Power() {
        KeyCode(26);
    }

    public void Up() {
        KeyCode(19);
    }

    public void Down() {
        KeyCode(20);
    }

    public void Left() {
        KeyCode(21);
    }

    public void Right() {
        KeyCode(22);
    }

    public void OK() {
        KeyCode(23);
    }

    public void VolumeUp() {
        KeyCode(24);
    }

    public void VolumeDown() {
        KeyCode(25);
    }

    public void Menu() {
        KeyCode(1);
    }

    public void Camera() {
        KeyCode(27);
    }

    public void Text(String text) {
        exec("input text " + text);
    }
}
