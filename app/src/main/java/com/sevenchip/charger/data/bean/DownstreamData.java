package com.sevenchip.charger.data.bean;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.sevenchip.charger.activity.SettingsFragment;
import com.sevenchip.charger.data.status.BatteryType;
import com.sevenchip.charger.data.status.WorkMode;
import com.sevenchip.charger.utils.ByteUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author : Alvin
 * create at : 2020/8/11 16:47
 * description : 下行数据 app发给设备
 * 总计29字节，16进制。从左到右为高字节-》低字节，MSB-》LSB。
 */
public class DownstreamData implements Serializable {

    private static final String TAG = DownstreamData.class.getSimpleName();

    /**
     * 引导头 暂时固定
     */
    private static byte[] LOADER_HEAD = new byte[]{(byte) 0x96, (byte) 0xfa, 0x00, 0x50};

    /**
     * 设备id 0x00000000000000000001
     * e.g 0x00 00 00 00 00 00 00 00 00 01
     */
    private String deviceID;

    /**
     * 通道号
     * 0/1
     * e.g 0x01 第二通道
     */
    private int channelNum;

    /**
     * 电池节数
     * 6-14
     * e.g 0x09 九节电池
     */
    private int cells;

    /**
     * 电池类型
     * 0-LiPo
     * 1-LiHv
     * 2-FULLY_MAX
     * 3-GENS_ACE
     * e.g 0x00（LiPo）
     */
    private @BatteryType
    int batteryType;


    /**
     * 工作模式
     * 0-保养
     * 3-充电
     * 6-放电
     * 9-平衡
     * e.g :0x03（充电）
     */
    private @WorkMode
    int workMode;

    /**
     * 冲放电量
     * 3位有效值，转10进制后，后1位为小数
     * e.g :0x00fe(十进制254，代表充电电流为25.4A）
     */
    private float current;

    /**
     * 状态
     * 0：开始
     * 1：停止
     * e.g :0x01（开始）
     */
    private int status;

    /**
     * 电池温度过高报警
     * 2位有效值
     * e.g :0x5c(十进制92）
     */
    private int temperatureAlarm;

    /**
     * 最大充电时长
     * 最大240
     * e.g :0x78(十进制120分钟）
     */
    private int maxChargingTime;

    /**
     * 最大电池容量
     * 最大60000
     * e.g :0xafc8（十进制32000）
     */
    private int maxBatteryCapacity;

    /**
     * 校验和
     */
    private int checksum;

    private byte[] data;

    public DownstreamData(Builder builder) {
        this.deviceID = builder.deviceID;
        this.batteryType = builder.batteryType;
        this.cells = builder.cells;
        this.channelNum = builder.channelNum;
        this.current = builder.current;
        this.maxBatteryCapacity = builder.maxBatteryCapacity;
        this.status = builder.status;
        this.maxChargingTime = builder.maxChargingTime;
        this.temperatureAlarm = builder.temperatureAlarm;
        this.workMode = builder.workMode;
        parse2ByteArray();
    }

    public byte[] parse2ByteArray() {
        this.data = new byte[29];
        byte[] bDeviceID = ByteUtils.asscllToByteArray(deviceID);
        System.arraycopy(bDeviceID, 0, data, 0, 10);

        data[10] = (byte) channelNum;
        data[11] = (byte) cells;
        data[12] = (byte) batteryType;
        data[13] = (byte) workMode;
        byte[] bCurrent = getCurrentBytes(current);
        data[14] = bCurrent[1];
        data[15] = bCurrent[0];
        data[16] = (byte) status;
        data[17] = (byte) temperatureAlarm;
        data[18] = (byte) maxChargingTime;
        byte[] bMaxBatteryCapacity = ByteUtils.int2ByteArrayMSB2(maxBatteryCapacity);
        data[19] = bMaxBatteryCapacity[0];
        data[20] = bMaxBatteryCapacity[1];
        data[21] = 0x00;
        data[22] = 0x00;

        data[23] = 0x00; //checksum
        data[24] = 0x00;

        data[25] = 0x7f;
        data[25] = LOADER_HEAD[0];
        data[26] = LOADER_HEAD[1];
        data[27] = LOADER_HEAD[2];
        data[28] = LOADER_HEAD[3];
        setChecksum();
        Log.e(SettingsFragment.TAG, "######### = > "+ByteUtils.byteArray2String(data));
        return data;
    }

    /**
     * @param deviceID
     * @return
     */
    private static String covertDeviceId(String deviceID) {
        char cc = 30;
        StringBuilder covertId = new StringBuilder();
        if (deviceID.length() == 20) {
            for (int i = 0; i < 10; i++) {
                char c = deviceID.charAt(i * 2 + 1);
                covertId.append(c);
            }
        }
        return covertId.toString();
    }


    //00-00-00-00-00-00-00-00-00-01-00-06-00-03-00-fe-01-5c-78-af-c8-00-00-05-34-96-fa-00-50
    public static DownstreamData parseBytes2DownstreamData(byte[] data) {
        DownstreamData downstreamData;
        if (data.length < 23) return null;

        byte[] deviceIdBytes = ByteUtils.capture(data, 0, 10);
        String deviceID = ByteUtils.byteArray2ASSCLL(deviceIdBytes);
        int channelNum = data[10];
        int cells = data[11];
        int batteryType = data[12];
        int workMode = data[13];
        float current = ByteUtils.byteToInt(new byte[]{0x00, 0x00, data[14], data[15]}) / 10.0f;
        int status = data[16];
        int temperatureAlarm = data[17];
        int maxChargingTime = data[18];
        int maxBatteryCapacity = ByteUtils.byteToInt(new byte[]{0x00, 0x00, data[19], data[20]});
        downstreamData = DownstreamData.createBuilder()
                .deviceID(deviceID)
                .channelNum(channelNum)
                .cells(cells)
                .batteryType(batteryType)
                .workMode(workMode)
                .current(current)
                .status(status)
                .temperatureAlarm(temperatureAlarm)
                .maxBatteryCapacity(maxBatteryCapacity)
                .maxChargingTime(maxChargingTime).build();
        return downstreamData;
    }

    private void setChecksum() {
        checksum = 0;
        for (byte aData : data) {
            checksum += aData & 0x00FF;
        }
        byte[] bChecksum = ByteUtils.int2ByteArrayMSB2(checksum);
        data[23] = bChecksum[0];
        data[24] = bChecksum[1];
    }

    /**
     * 冲放电量
     * 3位有效值，转10进制后，后1位为小数
     * e.g :0x00fe(十进制254，代表充电电流为25.4A）
     */
    private byte[] getCurrentBytes(float current) {
        int iCurrent = (int) (current * 10);
        return ByteUtils.int2ByteArrayLSB2(iCurrent);
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getCells() {
        return cells;
    }

    public void setCells(int cells) {
        this.cells = cells;
    }

    public int getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(@BatteryType int batteryType) {
        this.batteryType = batteryType;
    }

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(@WorkMode int workMode) {
        this.workMode = workMode;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTemperatureAlarm() {
        return temperatureAlarm;
    }

    public void setTemperatureAlarm(int temperatureAlarm) {
        this.temperatureAlarm = temperatureAlarm;
    }

    public int getMaxChargingTime() {
        return maxChargingTime;
    }

    public void setMaxChargingTime(int maxChargingTime) {
        this.maxChargingTime = maxChargingTime;
    }

    public int getMaxBatteryCapacity() {
        return maxBatteryCapacity;
    }

    public void setMaxBatteryCapacity(int maxBatteryCapacity) {
        this.maxBatteryCapacity = maxBatteryCapacity;
    }

    @Override
    public String toString() {
        return "DownstreamData{" +
                "deviceID=" + deviceID +
                ", channelNum=" + channelNum +
                ", cells=" + cells +
                ", batteryType=" + batteryType +
                ", workMode=" + workMode +
                ", current=" + current +
                ", status=" + status +
                ", temperatureAlarm=" + temperatureAlarm +
                ", maxChargingTime=" + maxChargingTime +
                ", maxBatteryCapacity=" + maxBatteryCapacity +
                '}';
    }

    public static class Builder {

        private String deviceID;

        private int channelNum;

        private int cells;

        private int batteryType;

        private int workMode;

        private float current;

        private int status;

        private int temperatureAlarm;

        private int maxChargingTime;

        private int maxBatteryCapacity;

        public Builder deviceID(String deviceID) {
            this.deviceID = deviceID.replace(" ", "");
            return this;
        }

        public Builder channelNum(int channelNum) {
            this.channelNum = channelNum;
            return this;
        }

        public Builder cells(int cells) {
            cells = Math.max(0, cells);
            cells = Math.min(14, cells);
            this.cells = cells;
            return this;
        }

        public Builder batteryType(@BatteryType int batteryType) {
            this.batteryType = batteryType;
            return this;
        }

        public Builder workMode(@WorkMode int workMode) {
            this.workMode = workMode;
            return this;
        }

        public Builder current(float current) {
            this.current = current;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder temperatureAlarm(int temperatureAlarm) {
            this.temperatureAlarm = temperatureAlarm;
            return this;
        }

        public Builder maxChargingTime(int maxChargingTime) {
            this.maxChargingTime = maxChargingTime;
            return this;
        }

        public Builder maxBatteryCapacity(int maxBatteryCapacity) {
            this.maxBatteryCapacity = maxBatteryCapacity;
            return this;
        }

        public DownstreamData build() {
            return new DownstreamData(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownstreamData that = (DownstreamData) o;
        return channelNum == that.channelNum &&
                cells == that.cells &&
                batteryType == that.batteryType &&
                workMode == that.workMode &&
                Float.compare(that.current, current) == 0 &&
                status == that.status &&
                temperatureAlarm == that.temperatureAlarm &&
                maxChargingTime == that.maxChargingTime &&
                maxBatteryCapacity == that.maxBatteryCapacity &&
                checksum == that.checksum &&
                deviceID.equals(that.deviceID) &&
                Arrays.equals(data, that.data);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        int result = Objects.hash(deviceID, channelNum, cells, batteryType, workMode, current, status, temperatureAlarm, maxChargingTime, maxBatteryCapacity, checksum);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
