package com.sevenchip.charger.data.bean;

import android.util.Log;

import java.io.Serializable;

/**
 * @author : Alvin
 * create at : 2020/8/17 11:58
 * description :
 */
public class BatteryCharger implements Serializable {
    private static final String TAG = "BatteryCharger";

    public static final int TIME_OUT = 3 * 1_000;

    private String deviceId;

    private UpstreamData CH01;
    private UpstreamData CH02;

    private long lastReceiveTimeCh1 = 0L;
    private long lastReceiveTimeCh2 = 0L;

    public long getLastReceiveTimeCh1() {
        return lastReceiveTimeCh1;
    }

    public void setLastReceiveTimeCh1(long lastReceiveTimeCh1) {
        this.lastReceiveTimeCh1 = lastReceiveTimeCh1;
    }

    public long getLastReceiveTimeCh2() {
        return lastReceiveTimeCh2;
    }

    public void setLastReceiveTimeCh2(long lastReceiveTimeCh2) {
        this.lastReceiveTimeCh2 = lastReceiveTimeCh2;
    }

    public boolean isCH1Online(){
        return System.currentTimeMillis() - lastReceiveTimeCh1 < TIME_OUT;
    }

    public boolean isCH2Online(){
        return System.currentTimeMillis() - lastReceiveTimeCh2 < TIME_OUT;
    }

    public BatteryCharger(UpstreamData upstreamData) {
        setChannel(upstreamData);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setChannel(UpstreamData upstreamData) {
        int num = upstreamData.getDownstreamData().getChannelNum();
        if (0 == num) {
            setCH01(upstreamData);
        } else if (1 == num) {
            setCH02(upstreamData);
        } else {
            Log.e(TAG, "异常数据通道 Channel num = " + num + ", data = " + upstreamData.toString());
        }
    }

    public UpstreamData getCH01() {
        return CH01;
    }

    public void setCH01(UpstreamData CH01) {
        String currentDeviceId = CH01.getDownstreamData().getDeviceID();
        if (deviceId == null || deviceId.equals(currentDeviceId)) {
            this.deviceId = currentDeviceId;
            this.CH01 = CH01;
            this.lastReceiveTimeCh1 = System.currentTimeMillis();
        } else {
            Log.e(TAG, "设置失败 本设备id = " + deviceId + ", 该数据id = " + currentDeviceId);
        }
    }

    public UpstreamData getCH02() {
        return CH02;
    }

    public void setCH02(UpstreamData CH02) {
        String currentDeviceId = CH02.getDownstreamData().getDeviceID();
        if (deviceId == null || deviceId.equals(currentDeviceId)) {
            this.deviceId = currentDeviceId;
            this.CH02 = CH02;
            this.lastReceiveTimeCh2 = System.currentTimeMillis();
        } else {
            Log.e(TAG, "设置失败 本设备id = " + deviceId + ", 该数据id = " + currentDeviceId);
        }
    }

}
