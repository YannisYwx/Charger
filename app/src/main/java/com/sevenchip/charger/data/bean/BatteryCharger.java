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

    private String deviceId;

    private UpstreamData CH01;
    private UpstreamData CH02;

    public BatteryCharger(UpstreamData upstreamData){
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
        } else {
            Log.e(TAG, "设置失败 本设备id = " + deviceId + ", 该数据id = " + currentDeviceId);
        }
    }

}
