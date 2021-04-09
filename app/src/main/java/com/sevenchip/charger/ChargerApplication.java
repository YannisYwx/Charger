package com.sevenchip.charger;

import android.annotation.SuppressLint;
import android.app.Application;

import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.utils.CrashHandler;
import com.sevenchip.charger.utils.WifiStatusChangedReceiver;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * @author : Alvin
 * create at : 2020/8/17 14:20
 * description :
 */
public class ChargerApplication extends Application {

    public static ChargerApplication instance;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        CrashHandler.getInstance().collectDeviceInfo(this);
        CrashHandler.getInstance().init(this);
        WifiStatusChangedReceiver.registered(this);
        WifiStatusChangedReceiver.getInstance().addWifStatusChangerListener(DataManager.getInstance());

        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)//这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
                .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
                .errorActivity(DefaultErrorActivity.class) //程序崩溃后显示的页面
                .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置
        CustomActivityOnCrash.install(this);
    }

    @Override
    public void onTerminate() {
        WifiStatusChangedReceiver.getInstance().removeAllListener();
        super.onTerminate();
    }
}
