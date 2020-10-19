package com.sevenchip.charger.data.status;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : Alvin
 * create at : 2020/8/12 15:57
 * description : The status of battery
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
 */
@IntDef({BatteryStatus.Standard, BatteryStatus.Full, BatteryStatus.ConnectionBreak,
        BatteryStatus.ReversePolarity, BatteryStatus.OverVoltage, BatteryStatus.LowVoltage,
        BatteryStatus.InputVolErr, BatteryStatus.BatteryDamaged, BatteryStatus.BalanceFinished,
        BatteryStatus.StoreIsOk, BatteryStatus.DischargeFinished})
@Retention(RetentionPolicy.SOURCE)
public @interface BatteryStatus {
    int Standard = 0;
    int Full = 1;
    int ConnectionBreak = 2;
    int ReversePolarity = 3;
    int OverVoltage = 4;
    int LowVoltage = 5;
    int InputVolErr = 6;
    int BatteryDamaged = 7;
    int BalanceFinished = 8;
    int StoreIsOk = 9;
    int DischargeFinished = 10;
}
