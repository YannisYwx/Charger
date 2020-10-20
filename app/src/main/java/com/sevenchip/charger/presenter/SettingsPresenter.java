package com.sevenchip.charger.presenter;

import com.sevenchip.charger.base.BasePresenter;
import com.sevenchip.charger.contract.SettingsContract;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.mode.SettingsMode;

/**
 * Author : Alvin
 * CreateTime : 2020/7/26 21:32
 * Email : 923080261@qq.com
 * Description :
 */
public class SettingsPresenter extends BasePresenter<SettingsContract.View, SettingsContract.Mode> implements SettingsContract.Presenter, DataManager.OnDataReceiveListener {
    private DownstreamData sendData;

    public SettingsPresenter(SettingsContract.View view) {
        super(view);
    }

    @Override
    public SettingsContract.Mode createMode() {
        return new SettingsMode();
    }


    @Override
    public void applySettings(DownstreamData data) {
        this.sendData = data;
        DataManager.getInstance().applySettings(data);
        DataManager.getInstance().addDataReceiveListener(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        DataManager.getInstance().removeDataReceiveListener(this);
    }

    @Override
    public void onDeviceDelete() {

    }

    @Override
    public void onDataReceive(UpstreamData data) {
        if(sendData.equals(data.getDownstreamData())) {
            mView.applySettingsResult(true);
        }
    }

    @Override
    public void onDataSendSuccess(DownstreamData data) {
        mView.sendSuccess();
    }

    @Override
    public void onDeviceOffline() {

    }
}
