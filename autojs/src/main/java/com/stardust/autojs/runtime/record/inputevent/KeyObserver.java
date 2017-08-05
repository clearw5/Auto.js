package com.stardust.autojs.runtime.record.inputevent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.KeyEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/4.
 */

public class KeyObserver implements InputEventObserver.InputEventListener {


    public static final int KEY_RESERVED = 0;
    public static final int KEY_ESC = 1;
    public static final int KEY_1 = 2;
    public static final int KEY_2 = 3;
    public static final int KEY_3 = 4;
    public static final int KEY_4 = 5;
    public static final int KEY_5 = 6;
    public static final int KEY_6 = 7;
    public static final int KEY_7 = 8;
    public static final int KEY_8 = 9;
    public static final int KEY_9 = 10;
    public static final int KEY_0 = 11;
    public static final int KEY_MINUS = 12;
    public static final int KEY_EQUAL = 13;
    public static final int KEY_BACKSPACE = 14;
    public static final int KEY_TAB = 15;
    public static final int KEY_Q = 16;
    public static final int KEY_W = 17;
    public static final int KEY_E = 18;
    public static final int KEY_R = 19;
    public static final int KEY_T = 20;
    public static final int KEY_Y = 21;
    public static final int KEY_U = 22;
    public static final int KEY_I = 23;
    public static final int KEY_O = 24;
    public static final int KEY_P = 25;
    public static final int KEY_LEFTBRACE = 26;
    public static final int KEY_RIGHTBRACE = 27;
    public static final int KEY_ENTER = 28;
    public static final int KEY_LEFTCTRL = 29;
    public static final int KEY_A = 30;
    public static final int KEY_S = 31;
    public static final int KEY_D = 32;
    public static final int KEY_F = 33;
    public static final int KEY_G = 34;
    public static final int KEY_H = 35;
    public static final int KEY_J = 36;
    public static final int KEY_K = 37;
    public static final int KEY_L = 38;
    public static final int KEY_SEMICOLON = 39;
    public static final int KEY_APOSTROPHE = 40;
    public static final int KEY_GRAVE = 41;
    public static final int KEY_LEFTSHIFT = 42;
    public static final int KEY_BACKSLASH = 43;
    public static final int KEY_Z = 44;
    public static final int KEY_X = 45;
    public static final int KEY_C = 46;
    public static final int KEY_V = 47;
    public static final int KEY_B = 48;
    public static final int KEY_N = 49;
    public static final int KEY_M = 50;
    public static final int KEY_COMMA = 51;
    public static final int KEY_DOT = 52;
    public static final int KEY_SLASH = 53;
    public static final int KEY_RIGHTSHIFT = 54;
    public static final int KEY_KPASTERISK = 55;
    public static final int KEY_LEFTALT = 56;
    public static final int KEY_SPACE = 57;
    public static final int KEY_CAPSLOCK = 58;
    public static final int KEY_F1 = 59;
    public static final int KEY_F2 = 60;
    public static final int KEY_F3 = 61;
    public static final int KEY_F4 = 62;
    public static final int KEY_F5 = 63;
    public static final int KEY_F6 = 64;
    public static final int KEY_F7 = 65;
    public static final int KEY_F8 = 66;
    public static final int KEY_F9 = 67;
    public static final int KEY_F10 = 68;
    public static final int KEY_NUMLOCK = 69;
    public static final int KEY_SCROLLLOCK = 70;
    public static final int KEY_KP7 = 71;
    public static final int KEY_KP8 = 72;
    public static final int KEY_KP9 = 73;
    public static final int KEY_KPMINUS = 74;
    public static final int KEY_KP4 = 75;
    public static final int KEY_KP5 = 76;
    public static final int KEY_KP6 = 77;
    public static final int KEY_KPPLUS = 78;
    public static final int KEY_KP1 = 79;
    public static final int KEY_KP2 = 80;
    public static final int KEY_KP3 = 81;
    public static final int KEY_KP0 = 82;
    public static final int KEY_KPDOT = 83;

    public static final int KEY_ZENKAKUHANKAKU = 85;
    public static final int KEY_102ND = 86;
    public static final int KEY_F11 = 87;
    public static final int KEY_F12 = 88;
    public static final int KEY_RO = 89;
    public static final int KEY_KATAKANA = 90;
    public static final int KEY_HIRAGANA = 91;
    public static final int KEY_HENKAN = 92;
    public static final int KEY_KATAKANAHIRAGANA = 93;
    public static final int KEY_MUHENKAN = 94;
    public static final int KEY_KPJPCOMMA = 95;
    public static final int KEY_KPENTER = 96;
    public static final int KEY_RIGHTCTRL = 97;
    public static final int KEY_KPSLASH = 98;
    public static final int KEY_SYSRQ = 99;
    public static final int KEY_RIGHTALT = 100;
    public static final int KEY_LINEFEED = 101;
    public static final int KEY_HOME = 102;
    public static final int KEY_UP = 103;
    public static final int KEY_PAGEUP = 104;
    public static final int KEY_LEFT = 105;
    public static final int KEY_RIGHT = 106;
    public static final int KEY_END = 107;
    public static final int KEY_DOWN = 108;
    public static final int KEY_PAGEDOWN = 109;
    public static final int KEY_INSERT = 110;
    public static final int KEY_DELETE = 111;
    public static final int KEY_MACRO = 112;
    public static final int KEY_MUTE = 113;
    public static final int KEY_VOLUMEDOWN = 114;
    public static final int KEY_VOLUMEUP = 115;
    public static final int KEY_POWER = 116;	/* SC System Power Down */
    public static final int KEY_KPEQUAL = 117;
    public static final int KEY_KPPLUSMINUS = 118;
    public static final int KEY_PAUSE = 119;
    public static final int KEY_SCALE = 120;	/* AL Compiz Scale (Expose) */

    public static final int KEY_KPCOMMA = 121;
    public static final int KEY_HANGEUL = 122;
    public static final int KEY_HANGUEL = KEY_HANGEUL;
    public static final int KEY_HANJA = 123;
    public static final int KEY_YEN = 124;
    public static final int KEY_LEFTMETA = 125;
    public static final int KEY_RIGHTMETA = 126;
    public static final int KEY_COMPOSE = 127;

    public static final int KEY_STOP = 128;	/* AC Stop */
    public static final int KEY_AGAIN = 129;
    public static final int KEY_PROPS = 130;	/* AC Properties */
    public static final int KEY_UNDO = 131;	/* AC Undo */
    public static final int KEY_FRONT = 132;
    public static final int KEY_COPY = 133;	/* AC Copy */
    public static final int KEY_OPEN = 134;	/* AC Open */
    public static final int KEY_PASTE = 135;	/* AC Paste */
    public static final int KEY_FIND = 136;	/* AC Search */
    public static final int KEY_CUT = 137;	/* AC Cut */
    public static final int KEY_HELP = 138;	/* AL Integrated Help Center */
    public static final int KEY_MENU = 139;	/* Menu (show menu) */
    public static final int KEY_CALC = 140;	/* AL Calculator */
    public static final int KEY_SETUP = 141;
    public static final int KEY_SLEEP = 142;	/* SC System Sleep */
    public static final int KEY_WAKEUP = 143;	/* System Wake Up */
    public static final int KEY_FILE = 144;	/* AL Local Machine Browser */
    public static final int KEY_SENDFILE = 145;
    public static final int KEY_DELETEFILE = 146;
    public static final int KEY_XFER = 147;
    public static final int KEY_PROG1 = 148;
    public static final int KEY_PROG2 = 149;
    public static final int KEY_WWW = 150;	/* AL Internet Browser */
    public static final int KEY_MSDOS = 151;
    public static final int KEY_COFFEE = 152;	/* AL Terminal Lock/Screensaver */
    public static final int KEY_SCREENLOCK = KEY_COFFEE;
    public static final int KEY_ROTATE_DISPLAY = 153;	/* Display orientation for e.g. tablets */
    public static final int KEY_DIRECTION = KEY_ROTATE_DISPLAY;
    public static final int KEY_CYCLEWINDOWS = 154;
    public static final int KEY_MAIL = 155;
    public static final int KEY_BOOKMARKS = 156;	/* AC Bookmarks */
    public static final int KEY_COMPUTER = 157;
    public static final int KEY_BACK = 158;	/* AC Back */
    public static final int KEY_FORWARD = 159;	/* AC Forward */
    public static final int KEY_CLOSECD = 160;
    public static final int KEY_EJECTCD = 161;
    public static final int KEY_EJECTCLOSECD = 162;
    public static final int KEY_NEXTSONG = 163;
    public static final int KEY_PLAYPAUSE = 164;
    public static final int KEY_PREVIOUSSONG = 165;
    public static final int KEY_STOPCD = 166;
    public static final int KEY_RECORD = 167;
    public static final int KEY_REWIND = 168;
    public static final int KEY_PHONE = 169;	/* Media Select Telephone */
    public static final int KEY_ISO = 170;
    public static final int KEY_CONFIG = 171;	/* AL Consumer Control Configuration */
    public static final int KEY_HOMEPAGE = 172;	/* AC Home */
    public static final int KEY_REFRESH = 173;	/* AC Refresh */
    public static final int KEY_EXIT = 174;	/* AC Exit */
    public static final int KEY_MOVE = 175;
    public static final int KEY_EDIT = 176;
    public static final int KEY_SCROLLUP = 177;
    public static final int KEY_SCROLLDOWN = 178;
    public static final int KEY_KPLEFTPAREN = 179;
    public static final int KEY_KPRIGHTPAREN = 180;
    public static final int KEY_NEW = 181;	/* AC New */
    public static final int KEY_REDO = 182;	/* AC Redo/Repeat */

    public static final int KEY_F13 = 183;
    public static final int KEY_F14 = 184;
    public static final int KEY_F15 = 185;
    public static final int KEY_F16 = 186;
    public static final int KEY_F17 = 187;
    public static final int KEY_F18 = 188;
    public static final int KEY_F19 = 189;
    public static final int KEY_F20 = 190;
    public static final int KEY_F21 = 191;
    public static final int KEY_F22 = 192;
    public static final int KEY_F23 = 193;
    public static final int KEY_F24 = 194;

    public static final int KEY_PLAYCD = 200;
    public static final int KEY_PAUSECD = 201;
    public static final int KEY_PROG3 = 202;
    public static final int KEY_PROG4 = 203;
    public static final int KEY_DASHBOARD = 204;	/* AL Dashboard */
    public static final int KEY_SUSPEND = 205;
    public static final int KEY_CLOSE = 206;	/* AC Close */
    public static final int KEY_PLAY = 207;
    public static final int KEY_FASTFORWARD = 208;
    public static final int KEY_BASSBOOST = 209;
    public static final int KEY_PRINT = 210;	/* AC Print */
    public static final int KEY_HP = 211;
    public static final int KEY_CAMERA = 212;
    public static final int KEY_SOUND = 213;
    public static final int KEY_QUESTION = 214;
    public static final int KEY_EMAIL = 215;
    public static final int KEY_CHAT = 216;
    public static final int KEY_SEARCH = 217;
    public static final int KEY_CONNECT = 218;
    public static final int KEY_FINANCE = 219;	/* AL Checkbook/Finance */
    public static final int KEY_SPORT = 220;
    public static final int KEY_SHOP = 221;
    public static final int KEY_ALTERASE = 222;
    public static final int KEY_CANCEL = 223;	/* AC Cancel */
    public static final int KEY_BRIGHTNESSDOWN = 224;
    public static final int KEY_BRIGHTNESSUP = 225;
    public static final int KEY_MEDIA = 226;

    public static final int KEY_SWITCHVIDEOMODE = 227;	/* Cycle between available video
                       outputs (Monitor/LCD/TV-out/etc) */
    public static final int KEY_KBDILLUMTOGGLE = 228;
    public static final int KEY_KBDILLUMDOWN = 229;
    public static final int KEY_KBDILLUMUP = 230;

    public static final int KEY_SEND = 231;	/* AC Send */
    public static final int KEY_REPLY = 232;	/* AC Reply */
    public static final int KEY_FORWARDMAIL = 233;	/* AC Forward Msg */
    public static final int KEY_SAVE = 234;	/* AC Save */
    public static final int KEY_DOCUMENTS = 235;

    public static final int KEY_BATTERY = 236;

    public static final int KEY_BLUETOOTH = 237;
    public static final int KEY_WLAN = 238;
    public static final int KEY_UWB = 239;

    public static final int KEY_UNKNOWN = 240;

    public static final int KEY_VIDEO_NEXT = 241;	/* drive next video source */
    public static final int KEY_VIDEO_PREV = 242;	/* drive previous video source */
    public static final int KEY_BRIGHTNESS_CYCLE = 243;	/* brightness up, after max is min */
    public static final int KEY_BRIGHTNESS_AUTO = 244;	/* Set Auto Brightness: manual
                      brightness control is off,
					  rely on ambient */
    public static final int KEY_BRIGHTNESS_ZERO = KEY_BRIGHTNESS_AUTO;
    public static final int KEY_DISPLAY_OFF = 245;	/* display device to off state */

    public static final int KEY_WWAN = 246;	/* Wireless WAN (LTE, UMTS, GSM, etc.) */
    public static final int KEY_WIMAX = KEY_WWAN;
    public static final int KEY_RFKILL = 247;	/* Key that controls all radios */

    public static final int KEY_MICMUTE = 248;

    public interface KeyListener {

        void onKeyDown(String keyName);

        void onKeyUp(String keyName);

    }

    private static final Map<String, Integer> keyNameToCode = new HashMap<>();
    private static final SparseArray<String> keyCodeToName = new SparseArray<>();

    private KeyListener mKeyListener;

    public KeyObserver() {

    }

    public void setKeyListener(KeyListener keyListener) {
        mKeyListener = keyListener;
    }

    @Override
    public void onInputEvent(@NonNull InputEventObserver.InputEvent event) {
        if (!event.type.equals("0001")) {
            return;
        }
        if (event.value.equalsIgnoreCase("00000000")) {
            notifyKeyUp(keyCodeToKeyName(Integer.parseInt(event.code, 16)));
        }
        if (event.value.equalsIgnoreCase("00000001")) {
            notifyKeyDown(keyCodeToKeyName(Integer.parseInt(event.code, 16)));
        }
    }

    public static String keyCodeToKeyName(int code) {
        return keyCodeToName.get(code);
    }


    public static int keyNameToCode(String name) {
        Integer code = keyNameToCode.get(name);
        if (code == null)
            return -1;
        return code;
    }

    private void notifyKeyDown(String keyName) {
        if (mKeyListener != null) {
            mKeyListener.onKeyDown(keyName);
        }
    }

    private void notifyKeyUp(String keyName) {
        if (mKeyListener != null) {
            mKeyListener.onKeyUp(keyName);
        }
    }

    static {
        try {
            for (Field field : KeyObserver.class.getFields()) {
                if (field.getName().startsWith("KEY_")) {
                    int keyCode = (int) field.get(null);
                    keyCodeToName.put(keyCode, field.getName());
                    keyNameToCode.put(field.getName(), keyCode);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
