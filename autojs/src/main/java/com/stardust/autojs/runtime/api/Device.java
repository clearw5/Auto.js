package com.stardust.autojs.runtime.api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.stardust.autojs.R;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.pio.PFiles;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ScreenMetrics;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import ezy.assist.compat.SettingsCompat;

/**
 * Created by Stardust on 2017/12/2.
 */

public class Device {

    public static final int width = ScreenMetrics.getDeviceScreenWidth();

    public static final int height = ScreenMetrics.getDeviceScreenHeight();

    public static final String buildId = Build.ID;

    public static final String buildDisplay = Build.DISPLAY;

    public static final String product = Build.PRODUCT;

    public static final String board = Build.BOARD;

    public static final String brand = Build.BRAND;

    public static final String device = Build.DEVICE;

    public static final String model = Build.MODEL;

    public static final String bootloader = Build.BOOTLOADER;

    public static final String hardware = Build.HARDWARE;

    public static final String fingerprint = Build.FINGERPRINT;

    public static final int sdkInt = Build.VERSION.SDK_INT;

    public static final String incremental = Build.VERSION.INCREMENTAL;

    public static final String release = Build.VERSION.RELEASE;

    public static final String baseOS;

    public static final String securityPatch;

    static {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            baseOS = Build.VERSION.BASE_OS;
            securityPatch = Build.VERSION.SECURITY_PATCH;
        } else {
            baseOS = null;
            securityPatch = null;
        }
    }

    public static final String codename = Build.VERSION.CODENAME;

    @SuppressLint("HardwareIds")
    public static final String serial = Build.SERIAL;

    private Context mContext;
    private PowerManager.WakeLock mWakeLock;
    private int mWakeLockFlag;

    public Device(Context context) {
        mContext = context;
    }

    @SuppressLint("HardwareIds")
    @Nullable
    public String getIMEI() {
        checkReadPhoneStatePermission();
        try {
            return ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (SecurityException e) {
            return null;
        }
    }


    @SuppressLint("HardwareIds")
    public String getAndroidId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public int getBrightness() throws Settings.SettingNotFoundException {
        return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
    }

    public int getBrightnessMode() throws Settings.SettingNotFoundException {
        return Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
    }

    public void setBrightness(int b) throws Settings.SettingNotFoundException {
        checkWriteSettingsPermission();
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, b);
    }

    public void setBrightnessMode(int b) throws Settings.SettingNotFoundException {
        checkWriteSettingsPermission();
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, b);
    }

    public int getMusicVolume() {
        return ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int getNotificationVolume() {
        return ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_NOTIFICATION);
    }

    public int getAlarmVolume() {
        return ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_ALARM);
    }

    public int getMusicMaxVolume() {
        return ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getNotificationMaxVolume() {
        return ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
    }

    public int getAlarmMaxVolume() {
        return ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_ALARM);
    }

    public void setMusicVolume(int i) {
        checkWriteSettingsPermission();
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
    }

    public void setAlarmVolume(int i) {
        checkWriteSettingsPermission();
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_ALARM, i, 0);
    }

    public void setNotificationVolume(int i) {
        checkWriteSettingsPermission();
        ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
                .setStreamVolume(AudioManager.STREAM_NOTIFICATION, i, 0);
    }

    public float getBattery() {
        Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return -1;
        }
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float battery = ((float) level / scale) * 100.0f;
        return Math.round(battery * 10) / 10;
    }

    public long getTotalMem() {
        ActivityManager activityManager = getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.totalMem;
    }

    public long getAvailMem() {
        ActivityManager activityManager = getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.availMem;
    }

    public boolean isCharging() {
        Intent intent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null) {
            throw new ScriptException("Cannot retrieve the battery state");
        }
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public void keepAwake(int flags, long timeout) {
        checkWakeLock(flags);
        mWakeLock.acquire(timeout);
    }

    @SuppressLint("WakelockTimeout")
    public void keepAwake(int flags) {
        checkWakeLock(flags);
        mWakeLock.acquire();
    }

    public boolean isScreenOn() {
        //按照API文档来说不应该使用PowerManager.isScreenOn()，但是，isScreenOn()和实际不一致的情况通常只会出现在安卓智能手表的类似设备上
        //因此这里仍然使用PowerManager.isScreenOn()
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        //   return ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getState() == Display.STATE_ON;
        //} else {
        return ((PowerManager) getSystemService(Context.POWER_SERVICE)).isScreenOn();
        //}
    }

    public void wakeUpIfNeeded() {
        if (!isScreenOn()) {
            wakeUp();
        }
    }

    public void wakeUp() {
        keepScreenOn(200);
    }

    public void keepScreenOn() {
        keepAwake(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP);
    }

    public void keepScreenOn(long timeout) {
        keepAwake(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, timeout);
    }

    public void keepScreenDim() {
        keepAwake(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP);
    }

    public void keepScreenDim(long timeout) {
        keepAwake(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, timeout);
    }

    private void checkWakeLock(int flags) {
        if (mWakeLock == null || flags != mWakeLockFlag) {
            cancelKeepingAwake();
            mWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(flags, Device.class.getName());
        }
    }

    public void cancelKeepingAwake() {
        if (mWakeLock != null && mWakeLock.isHeld())
            mWakeLock.release();
    }

    public void vibrate(long millis) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(millis);
    }

    public void cancelVibration() {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).cancel();
    }


    private void checkWriteSettingsPermission() {
        if (SettingsCompat.canWriteSettings(mContext)) {
            return;
        }
        SettingsCompat.manageWriteSettings(mContext);
        throw new SecurityException(mContext.getString(R.string.no_write_settings_permissin));
    }


    private void checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                throw new SecurityException(mContext.getString(R.string.no_read_phone_state_permissin));
            }
        }
    }


    // just to avoid warning of null pointer to make android studio happy..
    @NonNull
    @SuppressWarnings("unchecked")
    private <T> T getSystemService(String service) {
        Object systemService = mContext.getSystemService(service);
        if (systemService == null) {
            throw new RuntimeException("should never happen..." + service);
        }
        return (T) systemService;
    }

    private static final String FAKE_MAC_ADDRESS = "02:00:00:00:00:00";

    @SuppressLint("HardwareIds")
    public String getMacAddress() throws Exception {
        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMan == null) {
            return null;
        }
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        if (wifiInf == null) {
            return getMacByFile();
        }

        String mac = wifiInf.getMacAddress();
        if (FAKE_MAC_ADDRESS.equals(mac)) {
            mac = null;
        }
        if (mac == null) {
            mac = getMacByInterface();
            if (mac == null) {
                mac = getMacByFile();
            }
        }
        return mac;
    }

    private static String getMacByInterface() throws SocketException {
        List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface networkInterface : networkInterfaces) {
            if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder mac = new StringBuilder();
                for (byte b : macBytes) {
                    mac.append(String.format("%02X:", b));
                }

                if (mac.length() > 0) {
                    mac.deleteCharAt(mac.length() - 1);
                }
                return mac.toString();
            }
        }
        return null;
    }

    private static String getMacByFile() throws Exception {
        try {
            return PFiles.read("/sys/class/net/wlan0/address");
        } catch (UncheckedIOException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Device{" +
                "width=" + width +
                ", height=" + height +
                ", buildId='" + buildId + '\'' +
                ", buildDisplay='" + buildDisplay + '\'' +
                ", product='" + product + '\'' +
                ", board='" + board + '\'' +
                ", brand='" + brand + '\'' +
                ", device='" + device + '\'' +
                ", model='" + model + '\'' +
                ", bootloader='" + bootloader + '\'' +
                ", hardware='" + hardware + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", sdkInt=" + sdkInt +
                ", incremental='" + incremental + '\'' +
                ", release='" + release + '\'' +
                ", baseOS='" + baseOS + '\'' +
                ", securityPatch='" + securityPatch + '\'' +
                ", serial='" + serial + '\'' +
                '}';
    }

}
