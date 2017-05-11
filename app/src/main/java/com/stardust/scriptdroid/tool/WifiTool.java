package com.stardust.scriptdroid.tool;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Stardust on 2017/5/11.
 */

public class WifiTool {

    public static String getWifiAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }
}
