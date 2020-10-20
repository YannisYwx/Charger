package com.sevenchip.charger.presenter;

import android.os.Handler;
import android.util.Log;

import com.sevenchip.charger.base.BasePresenter;
import com.sevenchip.charger.contract.MainContract;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.mode.MainMode;
import com.sevenchip.charger.utils.ByteUtils;

import static com.sevenchip.charger.activity.SettingsFragment.TAG;

/**
 * Author : Alvin
 * CreateTime : 2020/7/26 21:32
 * Email : 923080261@qq.com
 * Description :
 */
public class MainPresenter extends BasePresenter<MainContract.View, MainContract.Mode> implements MainContract.Presenter, DataManager.OnDataReceiveListener {
    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    public MainContract.Mode createMode() {
        return new MainMode();
    }

    @Override
    public void startListenerPort() {
//        DataManager.getInstance().startTestDataTask();
        DataManager.getInstance().startListeningPort();
    }

    @Override
    public void stopListenerPort() {
//        DataManager.getInstance().stopTestDataTask();
        DataManager.getInstance().stopListeningPort();
    }

    @Override
    public void applySettings(DownstreamData data) {
        DataManager.getInstance().removeDataReceiveListener(this);
        DataManager.getInstance().applySettings(data);
        new Handler().postDelayed(() -> DataManager.getInstance().addDataReceiveListener(this), 1_000);
    }

    @Override
    public void onAttach() {
        DataManager.getInstance().addDataReceiveListener(this);
        super.onAttach();
    }

    @Override
    public void onDetach() {
        DataManager.getInstance().removeDataReceiveListener(this);
        super.onDetach();
    }

    @Override
    public void onDeviceDelete() {
        mView.refresh();
    }

    @Override
    public void onDataReceive(UpstreamData data) {
        mView.refresh();
        if (data == null) {
            mView.showMsg("receive error data");
        }
    }

    @Override
    public void onDataSendSuccess(DownstreamData data) {
        mView.showMsg("Settings success");
    }

    @Override
    public void onDeviceOffline() {
        mView.refresh();
    }
}
