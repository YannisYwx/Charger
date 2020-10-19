package com.sevenchip.charger;

import android.app.Application;

import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.utils.CrashHandler;
import com.sevenchip.charger.utils.WifiStatusChangedReceiver;

/**
 * @author : Alvin
 * create at : 2020/8/17 14:20
 * description :
 */
public class ChargerApplication extends Application {

    public static ChargerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        CrashHandler.getInstance().collectDeviceInfo(this);
        CrashHandler.getInstance().init(this);
        WifiStatusChangedReceiver.registered(this);
        WifiStatusChangedReceiver.getInstance().addWifStatusChangerListener(DataManager.getInstance());
    }

    @Override
    public void onTerminate() {
        WifiStatusChangedReceiver.getInstance().removeAllListener();
        super.onTerminate();
    }
}
