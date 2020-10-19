package com.sevenchip.charger.data.status;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : Alvin
 * create at : 2020/8/12 15:57
 * description : Wifi status
 */
@IntDef({WifiStatus.Close, WifiStatus.Open, WifiStatus.Disconnect, WifiStatus.Connect})
@Retention(RetentionPolicy.SOURCE)
public @interface WifiStatus {
    int Close = 0;
    int Open = 1;
    int Disconnect = 2;
    int Connect = 3;
}
