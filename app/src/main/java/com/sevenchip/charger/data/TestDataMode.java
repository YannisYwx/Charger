package com.sevenchip.charger.data;

import android.util.Log;

import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.status.BatteryType;
import com.sevenchip.charger.data.status.WorkMode;
import com.sevenchip.charger.utils.ByteUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/8/11 23:02
 * Email : 923080261@qq.com
 * Description :
 */
public class TestDataMode {
    private static final String TAG = TestDataMode.class.getSimpleName();

    public static byte[] getDownstreamData() {
        DownstreamData downstreamData = DownstreamData.createBuilder()
                .deviceID("00 00 00 00 00 00 00 00 00 01")
                .channelNum(0)
                .cells(6)
                .batteryType(BatteryType.LiPo)
                .workMode(WorkMode.Charging)
                .current(25.4f)
                .status(1)
                .temperatureAlarm(92)
                .maxBatteryCapacity(45000)
                .maxChargingTime(120)
                .build();
        byte[] data = downstreamData.parse2ByteArray();
        String dataStr = ByteUtils.byteArray2HexStringWithSpaces(data, data.length);
//        Log.d(TAG, dataStr);
//        Log.d(TAG, "解析前：" + downstreamData);
//        Log.d(TAG, "解析后：" + Objects.requireNonNull(DownstreamData.parseBytes2DownstreamData(data)).toString());
        return data;
    }

    public static void testUpstreamData() {
        String dataStr = "00000000000000000001" +
                "0006000300fe015c78af" +
                "c800000009cc07714756" +
                "00001f40000000000f02" +
                "0f020f020f020f020f02" +
                "0f020f020f020f020f02" +
                "0f020f020f02010a083a" +
                "27102600000000000008" +
                "cdf69a0800";

        Log.d(TAG, "dataStr = " + dataStr);
        Log.d(TAG, "dataStr.length = " + dataStr.length());
        byte[] data = ByteUtils.string2ByteArray(dataStr);
        Log.d(TAG, "data.length = " + data.length);
        Log.d(TAG, "解析前：" + ByteUtils.byteArray2HexStringWithSpaces(data, data.length));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
        Log.d(TAG, "解析后：" + upstreamData.toString());
        int checksum = UpstreamData.calculateChecksum(data);
        Log.d(TAG, "解析后：计算校验和" + checksum + ByteUtils.byteArray2HexStringWithSpaces(ByteUtils.intToByte(checksum), 4));
        Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());


    }

    public static void testUpstreamData1() {
        String dataStr1 = "00000000000000000001" +
                "01 0c 02 03 00ef 00 4d 78 7d" +
                "00 0000 00 0960 0708 32 50" +
                "00002ee0 00000000 13be" +
                "13be13be13be13be13be" +
                "13be13be13be13be13be" +
                "13be13be13be 00 0b022f" +
                "2710 3a 00000000 000011" +
                "ff f69a0800";
        Log.d(TAG, "dataStr = " + dataStr1);
        Log.d(TAG, "dataStr.length = " + dataStr1.length());
        byte[] data = ByteUtils.string2ByteArray(dataStr1);
        Log.d(TAG, "data.length = " + data.length);
        Log.d(TAG, "解析前：" + ByteUtils.byteArray2HexStringWithSpaces(data, data.length));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
        Log.d(TAG, "解析后：" + upstreamData.toString());
        int checksum = UpstreamData.calculateChecksum(data);
        Log.d(TAG, "解析后：计算校验和" + checksum + ByteUtils.byteArray2HexStringWithSpaces(ByteUtils.intToByte(checksum), 4));
        Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());


    }

    static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

    private static String getHHmmss() {
        String strDate = df.format(new Date());
        String[] dateArr = strDate.split(":");
        int hour = Integer.parseInt(dateArr[0]);
        int minute = Integer.parseInt(dateArr[1]);
        int second = Integer.parseInt(dateArr[2]);
        byte[] bDate = new byte[]{(byte) hour, (byte) minute, (byte) second};
        String sDate = ByteUtils.byteArray2String(bDate);
        Log.e(TAG, "当前时间" + sDate);
        return sDate;
    }


    public static byte[] getCH1111() {
        String temp = "30 30 30 30 30 36 37 33 36 30 " +
                      "00 00 00 03 13 88 01 00 00 00 " +
                "00 00 00 01 00 00 00 07 00 14 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 04 " +
                "49 F6 9A 08 00";
        String temp1 = "30 30 30 30 30 35 34 36 35 38" +
                "00 00 00 03 13 88 01 00 00 00 " +
                "00 00 00 01 00 00 00 09 00 14 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 04 " +
                "51 F6 9A 08 00 ";
        byte[] data = UpstreamData.setDataChecksum(ByteUtils.string2ByteArray(temp));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
        if (upstreamData != null) {
            Log.d(TAG, "解析后：" + upstreamData.toString());
            Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());
        }

        String deviceId = upstreamData.getDownstreamData().getDeviceID();
        String bs = ByteUtils.byteArray2HexStringWithSpaces(ByteUtils.asscllToByteArray(deviceId),10);
        Log.d(TAG, "deviceId：" + deviceId);
        Log.d(TAG, "bs：" + bs);
        return UpstreamData.setDataChecksum(data);
    }

    public static byte[] getCH1() {
        int p = new Random().nextInt(15) + 50;
        String pp = String.format("%02d", p);
        String currentTime = getHHmmss();
        String dataCH01 = "30 30 30 30 30 36 37 33 36 30 " +
                "00 06 00 03 00fe 01 5c78af" +
                "c800000009cc07714756" +
                "00001f40000000000f02" +
                "0f020f020f020f020f02" +
                "0f020f020f020f020f02" +
                "0f020f020f0201" + currentTime +
                "2710 " + pp + "00000000 00000000 f69a0800";
        byte[] data = UpstreamData.setDataChecksum(ByteUtils.string2ByteArray(dataCH01));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
//        if (upstreamData != null) {
//            Log.d(TAG, "解析后：" + upstreamData.toString());
//            Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());
//        }
        return UpstreamData.setDataChecksum(data);
    }

    public static byte[] getCH2() {
        String currentTime = getHHmmss();
        int p = new Random().nextInt(15) + 50;
        String pp = String.format("%02d", p);
        String temp = "30 30 30 30 30 35 34 36 35 38" +
                      "00 00 00 03 13 88 01 00 00 00 " +
                      "00 00 00 01 00 00 00 09 00 14 " +
                      "00 00 00 00 00 00 00 00 00 00 " +
                      "00 00 00 00 00 00 00 00 00 00 " +
                      "00 00 00 00 00 00 00 00 00 00 " +
                      "00 00 00 00 00 00 00 00 00 00 " +
                      "00 00 00 00 00 00 00 00 00 04 " +
                      "51 F6 9A 08 00 ";
        //67360
        String dataCH02 = "30 30 30 30 30 36 37 33 36 30 " +
                "01 0c 02 03 00ef 00 4d 78 7d" +
                "00 0000 00 0960 0708 32 50" +
                "00002ee0 00000000 13be" +
                "13be13be13be13be13be" +
                "13be13be13be13be13be" +
                "13be13be13be 00 " + currentTime +
                "2710 " + pp + "00000000 00000000 f69a0800";
        byte[] data = UpstreamData.setDataChecksum(ByteUtils.string2ByteArray(dataCH02));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
//        if (upstreamData != null) {
//            Log.d(TAG, "解析后：" + upstreamData.toString());
//            Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());
//        }
        return UpstreamData.setDataChecksum(data);
    }

    public static byte[] getCH1_2() {
        //戈西:
        // 00 00 00 00 00 00 00 00 00 00
        // 00 00 00 00 13 88 00 50 78 86
        // A0 00 00 04 69

        // 30 30 30 30 30 36 37 33 36 30
        // 00 06 00 03 00 FE 01 5C 78 AF
        // C8 00 00 07 29 96 FA 00 50

        // 30 30 30 30 30 36 37 33 36 30
        // 00 06 00 03 00 FE 00 5C 78 AF
        // C8 00 00 07 28 96 FA 00 50

        // 00 00 00 00 00 00 00 00 00 00
        // 00 00 00 00 A1 20 01 50 78 86
        // A0 00 00 04 90

        // 00 06 00 03 00  FE 01 5C 78 AF
        // 00 06 00 03 00  FE 00 5C 78 AF
        // C80000072896FA0050
        int p = new Random().nextInt(15) + 50;
        String pp = String.format("%02d", p);
        String currentTime = getHHmmss();
//                        "00 00 00 00 00 00 00 00 00 00 " +
//                        "00 00 00 00 13 88 00 50 78 86 " +19
//                        "A0 00 00 04 69 00 00 00 00 00 " +29
//                        "00 00 00 00 00 00 00 00 00 A1 " +38
//                        "20 01 50 78 86 A0 00 00 04 90 "
        //54658
        String dataCH01 = "30 30 30 30 30 35 34 36 35 38" +
                //通道 电池节数  电池类型  工作模式  电流25.36   star/stop 0/1  温度报警   充电时长  电池容量最大60000
                "00    0c       01       09       00ea         00             4f       78        afc8" +
                //   当前工作状态  总电压25.08  当前电流19.05    电池温度  充电器温度
                "0000 00          09cc           0771          47      56" +
                //电池容量  放电容量  单节电池电压 3.846
                "00001f40  00000000  0f02" +
                "0f020f020f020f020f02" +
                "0f020f020f020f020f02" +
                //            辅助状态    充电时长
                "0f020f020f02  00" + currentTime +
                //当前容量10000  百分比
                "2710         " + pp + "00000000 00000000 f69a0800";
        byte[] data = UpstreamData.setDataChecksum(ByteUtils.string2ByteArray(dataCH01));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
//        if (upstreamData != null) {
//            Log.d(TAG, "解析后：" + upstreamData.toString());
//            Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());
//        }
        return UpstreamData.setDataChecksum(data);
    }

    public static byte[] getCH2_2() {
        int p = new Random().nextInt(15) + 50;
        String pp = String.format("%02d", p);
        String currentTime = getHHmmss();
        String dataCH01 = "30 30 30 30 30 35 34 36 35 38" +
                //通道 电池节数  电池类型  工作模式  电流25.3    star/stop 0/1  温度报警  充电时长  电池容量最大60000
                "01    09       03       00       00cf         00             58       68        cf79" +
                //   当前工作状态  总电压25.08  当前电流19.05    电池温度  充电器温度
                "0000 00          0a58           0678          43      50" +
                //电池容量  放电容量  单节电池电压 3.846
                "00002f40  00000000  1203" +
                "05070fac0f590b020cf2" +
                "0ef20fb20cf20f021a02" +
                //            辅助状态    充电时长
                "0f020f020f02  07" + currentTime +
                //当前容量10000  百分比
                "3710         " + pp + "00000000 00000000 f69a0800";
        byte[] data = UpstreamData.setDataChecksum(ByteUtils.string2ByteArray(dataCH01));
        UpstreamData upstreamData = UpstreamData.parseBytes2UpstreamData(data);
//        if (upstreamData != null) {
//            Log.d(TAG, "解析后：" + upstreamData.toString());
//            Log.d(TAG, "解析后：是否正确" + upstreamData.isDataCorrect());
//        }
        return UpstreamData.setDataChecksum(data);
    }

}
