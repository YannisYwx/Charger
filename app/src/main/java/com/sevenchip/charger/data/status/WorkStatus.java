package com.sevenchip.charger.data.status;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : Alvin
 * create at : 2020/8/12 15:57
 * description : The work status of battery
 */
@IntDef({WorkStatus.Working, WorkStatus.Finished, WorkStatus.Error})
@Retention(RetentionPolicy.SOURCE)
public @interface WorkStatus {
    int Working = 0;
    int Finished = 1;
    int Error = 2;
}
