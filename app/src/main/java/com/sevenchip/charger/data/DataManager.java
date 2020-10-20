package com.sevenchip.charger.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sevenchip.charger.ChargerApplication;
import com.sevenchip.charger.data.bean.BatteryCharger;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.status.WifiStatus;
import com.sevenchip.charger.data.lan.udp.TCPManager;
import com.sevenchip.charger.data.lan.udp.UDPManager;
import com.sevenchip.charger.utils.NetworkUtils;
import com.sevenchip.charger.utils.PrefUtils;
import com.sevenchip.charger.utils.WifiStatusChangedReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Alvin
 * CreateTime : 2020/8/15 23:30
 * Description : 数据管理者 app所有的数据都是在这里读写控制
 */
public class DataManager implements UDPManager.OnLanMessageListener, WifiStatusChangedReceiver.OnWifStatusChangerListener {
    private static final String TAG = DataManager.class.getSimpleName();

    /**
     * The key of device id list for db
     */
    private static final String KEY_DEVICE_ID_LIST = "_KEY_DEVICE_ID_LIST";

    /**
     * 数据接收监听者 用来刷新UI数据
     */
    private List<OnDataReceiveListener> mReceiveListeners = new ArrayList<>();

    /**
     * 是否开始监听本地端口4000
     */
    private boolean isStartListener = false;

    private List<BatteryCharger> dataList = new ArrayList<>();

    private List<String> bindDeviceList;

    public List<BatteryCharger> getCurrentBatteryCharger() {
        return dataList;
    }

    public boolean isChannelOnline(UpstreamData upstreamData) {
        boolean isOnline = false;
        for (BatteryCharger batteryCharger : dataList) {
            if (batteryCharger.getDeviceId().equals(upstreamData.getDownstreamData().getDeviceID())) {
                isOnline = upstreamData.getDownstreamData().getChannelNum() == 0 ? batteryCharger.isCH1Online() : batteryCharger.isCH2Online();
            }
        }
        return isOnline;
    }

    @Override
    public void onDataReceive(byte[] data) {
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
        if (upstreamData != null) {
            Log.d(TAG, "接收到下行数据：" + upstreamData.toString());
            addToList(upstreamData);
        } else {
            Log.e(TAG, "接受数据解析异常");
            notifyDataChanged(null);
        }
    }

    @Override
    public void onDataSendComplete(byte[] data) {
        for (OnDataReceiveListener listener : mReceiveListeners) {
            listener.onDataSendSuccess(DownstreamData.parseBytes2DownstreamData(data));
        }
    }

    @Override
    public void onDataSendError(byte[] data, String errorMsg) {

    }

    @Override
    public void onDataReceiveError(String errorMsg) {

    }

    @Override
    public void onDeviceOffline() {
        for (OnDataReceiveListener listener : mReceiveListeners) {
            listener.onDeviceOffline();
        }
    }

    @Override
    public void onWifiStatusChanger(int status, String SSID) {
        if (status == WifiStatus.Close || status == WifiStatus.Disconnect) {
            //wifi关闭 停止监听4000端口号
            stopListeningPort();
        } else if (status == WifiStatus.Open || status == WifiStatus.Connect) {
            //wifi连接
            String hotspotAddress = NetworkUtils.getHotspotAddress(ChargerApplication.instance);
            if (!TextUtils.isEmpty(hotspotAddress)) {
                //开启监听4000端口号
                startListeningPort();
            }
        }
    }


    private DataManager() {
        bindDeviceList = getAllBindDeviceIds(ChargerApplication.instance);
    }

    private boolean isContains(String deviceID) {
        for (String id : bindDeviceList) {
            if (deviceID.contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加设备
     *
     * @param upstreamData 接收到的上行数据 一般为该充电的通道信息
     */
    private void addToList(UpstreamData upstreamData) {
        for (String id : bindDeviceList) {
            Log.d(TAG, "绑定的id = " + id);
        }
        if (bindDeviceList.isEmpty()) {
            Log.e(TAG, "绑定的设备为空");
            return;
        }
        String deviceId = upstreamData.getDownstreamData().getDeviceID();
        Log.d(TAG, "当前数据的id = " + deviceId);
        //只有在绑定列表的才有效
        if (isContains(deviceId)) {
            boolean isContain = false;
            for (BatteryCharger batteryCharger : dataList) {
                if (batteryCharger.getDeviceId().equals(deviceId)) {
                    isContain = true;
                    batteryCharger.setChannel(upstreamData);
                    break;
                }
            }
            if (!isContain) {
                dataList.add(new BatteryCharger(upstreamData));
            }
            notifyDataChanged(upstreamData);
        } else {
            Log.d(TAG, "当前数据不在绑定列表" + deviceId);
        }
    }

    /**
     * 通知所有的观察者（UI）
     *
     * @param upstreamData 下行数据 主要用来刷新主界面以及详情界面
     */
    private void notifyDataChanged(UpstreamData upstreamData) {
        for (OnDataReceiveListener listener : mReceiveListeners) {
            listener.onDataReceive(upstreamData);
        }
    }

    private static class DataManagerHolder {
        private static final DataManager INSTANCE = new DataManager();
    }

    public static DataManager getInstance() {
        return DataManagerHolder.INSTANCE;
    }

    /**
     * Get all bind device id list
     *
     * @param context 上下文
     * @return list of device id
     */
    public List<String> getAllBindDeviceIds(Context context) {
        List<String> deviceIdList = new ArrayList<>();
        String deviceIDListStr = PrefUtils.getStringFromPrefs(context, KEY_DEVICE_ID_LIST, "");
        if (!TextUtils.isEmpty(deviceIDListStr)) {
            String[] idList = deviceIDListStr.split("\\|");
            for (String id : idList) {
                if (!TextUtils.isEmpty(id)) {
                    deviceIdList.add(id);
                    Log.d(TAG, "device id = " + id);
                }
            }
        }
        return deviceIdList;
    }

    /**
     * 保存设备ID
     *
     * @param deviceId 设备id
     * @param context  上下文
     * @return true 保存成功 ， false 保存失败
     */
    public boolean saveDevice(Context context, String deviceId) {
        String deviceIDListStr = PrefUtils.getStringFromPrefs(context, KEY_DEVICE_ID_LIST, "");
        if (deviceIDListStr.contains(deviceId)) {
            return false;
        }
        deviceIDListStr = (deviceIDListStr + "|" + deviceId).trim();
        PrefUtils.saveStringToPrefs(context, KEY_DEVICE_ID_LIST, deviceIDListStr);
        bindDeviceList.add(deviceId);
        return true;
    }

    private void deleteBatteryCharger(String deviceId) {
        BatteryCharger batteryCharger = null;
        for (BatteryCharger temp : dataList) {
            if (temp.getDeviceId().contains(deviceId)) {
                batteryCharger = temp;
                break;
            }
        }
        if (batteryCharger != null) {
            dataList.remove(batteryCharger);
        }
        for (OnDataReceiveListener listener : mReceiveListeners) {
            listener.onDeviceDelete();
        }
    }

    /**
     * 删除数据里绑定的数据
     *
     * @param context  上下文
     * @param deviceId 设备id
     * @return true ：删除成功 。 false :删除失败
     */
    public boolean deleteDevice(Context context, String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return false;
        }
        if (bindDeviceList.contains(deviceId)) {
            bindDeviceList.remove(deviceId);
            //删除当前设备
            deleteBatteryCharger(deviceId);
        } else {
            return false;
        }
        String deviceIDListStr = "";
        if (bindDeviceList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String id : bindDeviceList) {
                sb.append(id).append("|");
            }
            deviceIDListStr = sb.toString();
        }
        PrefUtils.saveStringToPrefs(context, KEY_DEVICE_ID_LIST, deviceIDListStr);
        return true;
    }

    /**
     * 开始监听4000端口
     */
    public void startListeningPort() {
        if (!isStartListener) {
            isStartListener = true;
            UDPManager.getInstance().addLanMessageListener(this);
            UDPManager.getInstance().startListenerBroadcast();
        }
    }

    /**
     * 停止监听端口4000
     */
    public void stopListeningPort() {
        if (isStartListener) {
            isStartListener = false;
            UDPManager.getInstance().removeLanMessageListener(this);
            UDPManager.getInstance().stopListenerBroadcast();
        }
    }

    public void startTestDataTask() {
        if (!isStartListener) {
            isStartListener = true;
            UDPManager.getInstance().addLanMessageListener(this);
            UDPManager.getInstance().startTestTask();
        }
    }

    public void stopTestDataTask() {
        if (isStartListener) {
            isStartListener = false;
            UDPManager.getInstance().removeLanMessageListener(this);
            UDPManager.getInstance().stopTestTask();
        }
    }

    public void applySettings(DownstreamData data) {
        UDPManager.getInstance().sendMessage(data.parse2ByteArray());
    }

    /**
     * 设置wifi账号密码
     *
     * @param hotspot     热点地址
     * @param wifiAccount wifi 账号
     * @param password    wifi密码
     */
    public void settingWifiAccount(String hotspot, String wifiAccount, String password) {
        String message = "ssid@" + wifiAccount + ",pswd@" + password + ",end@";
        TCPManager.getInstance().sendMessage(hotspot, 5050, message);
    }

    public BatteryCharger getBatteryChargerById(String deviceId) {
        if (dataList.isEmpty()) return null;
        for (BatteryCharger batteryCharger : dataList) {
            if (batteryCharger.getDeviceId().equals(deviceId) || batteryCharger.getDeviceId().contains(deviceId)) {
                return batteryCharger;
            }
        }
        return null;
    }


    public void addDataReceiveListener(OnDataReceiveListener listener) {
        if (!mReceiveListeners.contains(listener)) {
            mReceiveListeners.add(listener);
        }
    }

    public void removeDataReceiveListener(OnDataReceiveListener listener) {
        mReceiveListeners.remove(listener);
    }

    public interface OnDataReceiveListener {

        void onDeviceDelete();

        void onDataReceive(UpstreamData data);

        void onDataSendSuccess(DownstreamData data);

        void onDeviceOffline();
    }

}
