package com.sevenchip.charger.data.status;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : Alvin
 * create at : 2020/8/12 15:57
 * description : The work mode of battery
 */
@IntDef({WorkMode.Upkeep, WorkMode.Charging, WorkMode.Discharging, WorkMode.Balance})
@Retention(RetentionPolicy.SOURCE)
public @interface WorkMode {
    int Upkeep = 0;
    int Charging = 3;
    int Discharging = 6;
    int Balance = 9;
}
