package com.sevenchip.charger.data.status;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : Alvin
 * create at : 2020/8/12 15:57
 * description : The type of battery
 */
@IntDef({BatteryType.LiPo, BatteryType.LiHv, BatteryType.FULLY_MAX,BatteryType.GENS_ACE})
@Retention(RetentionPolicy.SOURCE)
public @interface BatteryType {
    int LiPo = 0;
    int LiHv = 1;
    int FULLY_MAX = 2;
    int GENS_ACE = 3;
}
