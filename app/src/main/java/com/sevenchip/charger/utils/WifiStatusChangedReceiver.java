package com.sevenchip.charger.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.sevenchip.charger.data.status.WifiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Alvin
 * CreateTime : 2020/8/17 0:04
 * Description : 监控wifi的状态改变
 */
public class WifiStatusChangedReceiver extends BroadcastReceiver {
    private static final String TAG = WifiStatusChangedReceiver.class.getSimpleName();

    private static WifiStatusChangedReceiver instance;

    private List<OnWifStatusChangerListener> listeners;

    private WifiStatusChangedReceiver() {
        listeners = new ArrayList<>();
    }

    public static WifiStatusChangedReceiver getInstance() {
        if (instance == null) {
            synchronized (WifiStatusChangedReceiver.class) {
                if (instance == null) {
                    instance = new WifiStatusChangedReceiver();
                }
            }
        }
        return instance;
    }

    public static void registered(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.getApplicationContext().registerReceiver(getInstance(), filter);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String info = printBundle(extras);
            Log.i(TAG, "extras = " + info);
        }
        if (action == null) return;
        if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
            Log.i(TAG, "wifi信号强度变化");
        }
        //wifi连接上与否
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                Log.i(TAG, "wifi断开");
                postStatus(WifiStatus.Disconnect, null);
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //获取当前wifi名称
                Log.i(TAG, "连接到网络 " + wifiInfo.getSSID());
                postStatus(WifiStatus.Connect, wifiInfo.getSSID());
            }
        }
        //wifi打开与否
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                Log.i(TAG, "系统关闭wifi");
                postStatus(WifiStatus.Close, null);
            } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                Log.i(TAG, "系统开启wifi");
                postStatus(WifiStatus.Open, null);
            }
        }
    }

    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(WifiManager.EXTRA_WIFI_STATE)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
            } else {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.get(key));
            }
        }
        return sb.toString();
    }

    public interface OnWifStatusChangerListener {
        void onWifiStatusChanger(@WifiStatus int status, String SSID);
    }

    public void addWifStatusChangerListener(OnWifStatusChangerListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeWifStatusChangerListener(OnWifStatusChangerListener listener) {
        if (listeners != null && !listeners.isEmpty()) {
            if (!listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    private void postStatus(@WifiStatus int status, String SSID) {
        if (listeners.isEmpty()) return;
        for (OnWifStatusChangerListener listener : listeners) {
            listener.onWifiStatusChanger(status, SSID);
        }
    }

    public void removeAllListener() {
        listeners.clear();
        listeners = null;
    }

}
