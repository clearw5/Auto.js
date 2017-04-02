package autojs.runtime.api;

/**
 * Created by Stardust on 2017/3/7.
 */

public class Shell extends com.stardust.scriptdroid.tool.Shell {

    public Shell(boolean root) {
        super(root);
    }

    public void Tap(int x, int y) {
        execute("input tap " + x + " " + y);
    }

    public void Swipe(int x1, int y1, int x2, int y2) {
        execute("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2);
    }

    public void Swipe(int x1, int y1, int x2, int y2, long duration) {
        execute("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2 + " " + duration);
    }

    public void KeyCode(int keyCode) {
        execute("input keyevent " + keyCode);
    }

    public void KeyCode(String keyCode) {
        execute("input keyevent " + keyCode);
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
        execute("input text " + text);
    }

}
