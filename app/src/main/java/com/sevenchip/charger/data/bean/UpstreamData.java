package com.sevenchip.charger.data.bean;

import android.util.Log;

import com.sevenchip.charger.data.status.BatteryStatus;
import com.sevenchip.charger.utils.AppUIFormatUtils;
import com.sevenchip.charger.utils.ByteUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author : Alvin
 * create at : 2020/8/11 16:47
 * description : 上行数据 设备发给app
 * 总计85字节，16进制。从左到右为高字节-》低字节，MSB-》LSB。
 */
public class UpstreamData implements Serializable {

    private static final String TAG = UpstreamData.class.getSimpleName();
    private static int DATA_LENGTH = 85;

    /**
     * 引导头 暂时固定 f6 9a 08 00
     */
    private static byte[] LOADER_HEAD = new byte[]{(byte) 0xf6, (byte) 0x9a, 0x08, 0x00};

    /**
     * 上行数据中包含了 下行数据
     */
    private DownstreamData downstreamData;

    /**
     * 当前工作状态
     * 0：开始
     * 1：停止
     * e.g :0x01（停止）
     */
    private int workStatus;

    /**
     * 总电压
     * 4位有效值，转10进制后，后2位为小数
     * e.g :0x09cc(十进制2508，代表电压总电压为25.08V）
     */
    private float totalVoltage;


    /**
     * 当前冲放电电流
     * 4位有效值，转10进制后，后2位为小数
     * e.g :0x771（十进制1905，代表当前电流为19.05A）
     */
    private float currentCurrent;

    /**
     * 当前电池温度
     * e.g : 0x47（十进制71摄氏度）
     */
    private int batteryTemperature;


    /**
     * 当前充电器温度
     * e.g : 0x47（十进制71摄氏度）
     */
    private int chargerTemperature;

    /**
     * 充电容量
     * 4位有效值
     * 0x00 00 1f 40（十进制8000mah）
     */
    private int chargingCapacity;

    /**
     * 放电容量
     * 4位有效值
     * 0x00 00 1f 40（十进制8000mah）
     */
    private int dischargingCapacity;

    /**
     * 单节电池电压
     * 4位有效值，转10进制后，后3位为小数
     * e.g : 0x0f02（十进制3842，代表本节电池电压为3.842v）
     */
    private float[] _BVXS;

    /**
     * 电池辅助状态
     * 0-Standby标准/
     * 1-Full充电充满/
     * 2-Connection break电池连接异常/
     * 3-Reverse polarity电池极性接反/
     * 4-OverVoltage电池电压过高/
     * 5-Low voltage电池电压过低/
     * 6-Input vol err输入电压异常/
     * 7-Battery damaged电池损坏/
     * 8-Balance is finished平衡完成/
     * 9-Store is ok保养完成/
     * 10-Discharge is finished放电完成
     * 11-Over Temperature 过温保护
     * 12-Over Time 超时保护
     */
    @BatteryStatus
    private int batteryStatus;

    /**
     * 充电时长
     * hh-mm-ss
     * 0x0a083a（十进制10-08-58，代表当前已经充电10小时8分58秒）
     */
    private String chargingTime;

    /**
     * 当前充电容量
     * 0x2710（十进制10000）
     */
    private int currentCapacity;

    /**
     * 充电百分比
     * 0x26（十进制38，代表当前充电进度为38%）
     */
    private int chargingPercent;

    /**
     * 校验和 4位
     * 除校验和外的81字节累加
     * 0x00 00 08 cd
     */
    private int checksum;

    private byte[] data;

    public static UpstreamData parseBytes2UpstreamData(byte[] data) {
        if (data == null || data.length != 85) {
            Log.e(TAG, "下行数据异常");
            return null;
        }
        return new UpstreamData(data);
    }

    private UpstreamData(byte[] data) {
        this.data = data;
        this.downstreamData = DownstreamData.parseBytes2DownstreamData(data);
        this.workStatus = data[23];
        this.totalVoltage = ByteUtils.byteArray2Int(data, 24, 2, false) / 100.00f;
        this.currentCurrent = ByteUtils.byteArray2Int(data, 26, 2, false) / 10.00f;
        this.batteryTemperature = data[28];
        this.chargerTemperature = data[29];
        this.chargingCapacity = ByteUtils.byteArray2Int(data, 30, 4, false);
        this.dischargingCapacity = ByteUtils.byteArray2Int(data, 34, 4, false);
        this._BVXS = new float[14];
        for (int i = 0; i < 14; i++) {
            _BVXS[i] = ByteUtils.byteArray2Int(data, 38 + 2 * i, 2, false) / 1000.0f;
        }
        this.batteryStatus = data[66];
        this.chargingTime = AppUIFormatUtils.getChargerDuration(data[67], data[68], data[69]);
        this.currentCapacity = ByteUtils.byteArray2Int(data, 70, 2, false);
        this.chargingPercent = data[72];
        this.checksum = ByteUtils.byteArray2Int(data, 77, 4, false);
    }

    public static int calculateChecksum(byte[] data) {
        int checksum = 0;
        for (int i = 0; i < 85; i++) {
            if (i < 77 || i > 80) {
                checksum += data[i] & 0x00FF;
            }
        }
        return checksum;
    }

    public static byte[] setDataChecksum(byte[] data) {
        int checksum = calculateChecksum(data);
        byte[] bc = ByteUtils.intToByte(checksum);
        data[77] = bc[0];
        data[78] = bc[1];
        data[79] = bc[2];
        data[80] = bc[3];
        return data;
    }

    public String getStatusInfo() {
        return AppUIFormatUtils.getStatusInfo(downstreamData.getCells(), downstreamData.getBatteryType(), batteryStatus);
    }

    public DownstreamData getDownstreamData() {
        return downstreamData;
    }

    public void setDownstreamData(DownstreamData downstreamData) {
        this.downstreamData = downstreamData;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public float getTotalVoltage() {
        return totalVoltage;
    }

    public void setTotalVoltage(float totalVoltage) {
        this.totalVoltage = totalVoltage;
    }

    public float getCurrentCurrent() {
        return currentCurrent;
    }

    public void setCurrentCurrent(float currentCurrent) {
        this.currentCurrent = currentCurrent;
    }

    public int getBatteryTemperature() {
        return batteryTemperature;
    }

    public void setBatteryTemperature(int batteryTemperature) {
        this.batteryTemperature = batteryTemperature;
    }

    public int getChargerTemperature() {
        return chargerTemperature;
    }

    public void setChargerTemperature(int chargerTemperature) {
        this.chargerTemperature = chargerTemperature;
    }

    public int getChargingCapacity() {
        return chargingCapacity;
    }

    public void setChargingCapacity(int chargingCapacity) {
        this.chargingCapacity = chargingCapacity;
    }

    public int getDischargingCapacity() {
        return dischargingCapacity;
    }

    public void setDischargingCapacity(int dischargingCapacity) {
        this.dischargingCapacity = dischargingCapacity;
    }

    public float[] get_BVXS() {
        return _BVXS;
    }

    public String getSingleVoltage(int index){
        // 四舍五入
        BigDecimal value = BigDecimal.valueOf(_BVXS[index]).setScale(2,BigDecimal.ROUND_HALF_UP);
        // 不足两位小数补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(value);
    }


    public void set_BVXS(float[] _BVXS) {
        this._BVXS = _BVXS;
    }

    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getChargingTime() {
        return chargingTime;
    }

    public void setChargingTime(String chargingTime) {
        this.chargingTime = chargingTime;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public int getChargingPercent() {
        return chargingPercent;
    }

    public void setChargingPercent(int chargingPercent) {
        this.chargingPercent = chargingPercent;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UpstreamData{" +
                "downstreamData=" + downstreamData +
                ", workStatus=" + workStatus +
                ", totalVoltage=" + totalVoltage +
                ", currentCurrent=" + currentCurrent +
                ", batteryTemperature=" + batteryTemperature +
                ", chargerTemperature=" + chargerTemperature +
                ", chargingCapacity=" + chargingCapacity +
                ", dischargingCapacity=" + dischargingCapacity +
                ", _BVXS=" + Arrays.toString(_BVXS) +
                ", batteryStatus=" + batteryStatus +
                ", chargingTime='" + chargingTime + '\'' +
                ", currentCapacity=" + currentCapacity +
                ", chargingPercent=" + chargingPercent +
                ", checksum=" + checksum +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public boolean isDataCorrect() {
        int tempChecksum = 0;
        for (int i = 0; i < 85; i++) {
            if (i < 77 || i > 80) {
                tempChecksum += data[i] & 0x00FF;
            }
        }
        return tempChecksum == checksum;
    }

    public static void getDefaultData(){

    }

}
