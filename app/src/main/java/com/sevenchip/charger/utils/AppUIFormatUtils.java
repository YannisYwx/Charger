package com.sevenchip.charger.utils;

import android.support.annotation.ColorRes;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sevenchip.charger.ChargerApplication;
import com.sevenchip.charger.R;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.status.BatteryStatus;
import com.sevenchip.charger.data.status.BatteryType;
import com.sevenchip.charger.data.status.WorkStatus;

/**
 * Author : Alvin
 * CreateTime : 2020/8/18 21:59
 * Description :
 */
public class AppUIFormatUtils {

    private static String[] BATTERY_TYPE1 = ChargerApplication.instance.getResources().getStringArray(R.array.battery_type);
    private static String[] BATTERY_STATUS1 = ChargerApplication.instance.getResources().getStringArray(R.array.battery_status);
    private static String[] WORK_MODE1 = ChargerApplication.instance.getResources().getStringArray(R.array.work_mode);

    public static String getStatusInfo(int cell, @BatteryType int batteryType, int status) {
        return cell + "s" + " " + ChargerApplication.instance.getResources().getStringArray(R.array.battery_type)[batteryType] + ChargerApplication.instance.getResources().getStringArray(R.array.battery_status)[status];
    }

    /**
     * 获取当前通道的状态信息
     *
     * @param textView     需要设置的textView
     * @param upstreamData 当前的上行数据
     * @return 状态信息
     */
    public static void setChargerStatusInfo(TextView textView, UpstreamData upstreamData) {
        String statusInfo = null;
        int workMode = upstreamData.getDownstreamData().getWorkMode();
        workMode = Math.max(0, workMode);
        workMode = Math.min(9, workMode);
        int batteryStatus = upstreamData.getBatteryStatus();
        batteryStatus = Math.max(0, batteryStatus);
        batteryStatus = Math.min(12, batteryStatus);
        int cell = upstreamData.getDownstreamData().getCells();
        int batteryType = upstreamData.getDownstreamData().getBatteryType();
        batteryType = Math.max(0, batteryType);
        batteryType = Math.min(3, batteryType);
        int backgroundRes = R.drawable.bg_status_charging;
        @ColorRes int textColor = R.color.chocolate;
        @WorkStatus int workStatus = getChargerWorkStatus(upstreamData);
        if (workStatus == WorkStatus.Working) {
            backgroundRes = R.drawable.bg_status_charging;
            textColor = R.color.chocolate;
            statusInfo = cell + "s" + " " + ChargerApplication.instance.getResources().getStringArray(R.array.battery_type)[batteryType] + " " + ChargerApplication.instance.getResources().getStringArray(R.array.work_mode)[workMode / 3];
        } else if (workStatus == WorkStatus.Finished) {
            backgroundRes = R.drawable.bg_status_full;
            textColor = R.color.themeBlue;
            statusInfo = cell + "s" + " " + ChargerApplication.instance.getResources().getStringArray(R.array.battery_type)[batteryType] + " " + ChargerApplication.instance.getResources().getStringArray(R.array.battery_status)[batteryStatus];
        } else if (workStatus == WorkStatus.Error) {
            backgroundRes = R.drawable.bg_status_error;
            textColor = R.color.real_red;
            statusInfo = ChargerApplication.instance.getResources().getStringArray(R.array.battery_status)[batteryStatus];
        }
        textView.setBackgroundResource(backgroundRes);
        textView.setTextColor(ChargerApplication.instance.getResources().getColor(textColor));
        textView.setText(statusInfo);
    }

    public static void setOfflineStatus(TextView textView) {
        textView.setText(R.string.offline);
        textView.setBackgroundResource(R.drawable.bg_status_full);
        textView.setTextColor(ChargerApplication.instance.getResources().getColor(R.color.themeBlue));
    }

    /**
     * 获取充电状态详情的样式
     *
     * @return 1： 工作中， 2： 完成， 3 异常
     */
    public static int getChargerWorkStatus(UpstreamData upstreamData) {
        int chargerStatus = upstreamData.getBatteryStatus();
        switch (chargerStatus) {
            case BatteryStatus.Standard:
                //工作中 标准状态
                return WorkStatus.Working;
            case BatteryStatus.Full:
            case BatteryStatus.BalanceFinished:
            case BatteryStatus.StoreIsOk:
            case BatteryStatus.DischargeFinished:
                //工作完成 对应四种工作模式
                return WorkStatus.Finished;
            default:
                //其他 异常状态
                return WorkStatus.Error;
        }
    }

    /**
     * 获取当前电池类型
     *
     * @param upstreamData 当前的上行数据
     * @return 电池类型
     */
    public static String getBatteryType(UpstreamData upstreamData) {
        return ChargerApplication.instance.getResources().getStringArray(R.array.battery_type)[upstreamData.getDownstreamData().getBatteryType()];
    }

    public static String getChargerDuration(int hour, int minute, int second) {
        return ChargerApplication.instance.getString(R.string.charger_duration, hour, minute, second);
    }

    /**
     * 获取辅助状态详情
     *
     * @param upstreamData 当前的上行数据
     * @return 辅助状态
     */
    public static String getCPAInfo(UpstreamData upstreamData) {
        return ChargerApplication.instance.getResources().getStringArray(R.array.battery_status)[upstreamData.getBatteryStatus()];
    }

    public static String getBatteryId(UpstreamData upstreamData) {
        String deviceId = upstreamData.getDownstreamData().getDeviceID();
        return deviceId.substring(5);
    }

    public static String getChannelNo(UpstreamData upstreamData) {
        int res = upstreamData.getDownstreamData().getChannelNum() == 0 ? R.string.channel_1 : R.string.channel_2;
        return ChargerApplication.instance.getString(res);
    }

    public static int getData_BatteryType(RadioGroup group) {
        int batteryType = 0;
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_LiPo:
                batteryType = 0;
                break;
            case R.id.rb_LiHv:
                batteryType = 1;
                break;
            case R.id.rb_FULLY_MAX:
                batteryType = 2;
                break;
            case R.id.rb_GENS_ACE:
                batteryType = 3;
                break;
            default:
                break;
        }
        return batteryType;
    }

    public static int getData_WorkModeData(RadioGroup group) {
        int workMode = 0;
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_upkeep:
                workMode = 0;
                break;
            case R.id.rb_charging:
                workMode = 3;
                break;
            case R.id.rb_discharge:
                workMode = 6;
                break;
            case R.id.rb_balance:
                workMode = 9;
                break;
            default:
                break;
        }
        return workMode;
    }

}
