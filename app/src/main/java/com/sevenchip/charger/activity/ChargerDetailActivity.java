package com.sevenchip.charger.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.utils.AppUIFormatUtils;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.widget.ChargerPropertyView;
import com.sevenchip.charger.widget.VoltageView;

import butterknife.BindView;

/**
 * Author : Alvin
 * CreateTime : 2020/7/26 22:49
 * Description :
 */
public class ChargerDetailActivity extends BaseActivity implements DataManager.OnDataReceiveListener {
    public static final String KEY_CHANNEL = "_KEY_CHANNEL";

    @BindView(R.id.tv_charger_name)
    TextView tvChargerId;
    @BindView(R.id.tv_channel_name)
    TextView tvChannel;
    @BindView(R.id.tv_status_info)
    TextView tvStatus;
    @BindView(R.id.cv_battery_type)
    ChargerPropertyView cpvBatteryType;
    @BindView(R.id.cv_battery_cells)
    ChargerPropertyView cpvBatteryCells;
    @BindView(R.id.cv_battery_id)
    ChargerPropertyView cpvBatteryId;
    @BindView(R.id.cv_battery_temperature)
    ChargerPropertyView cpvBatteryTemperature;
    @BindView(R.id.cv_charger_temperature)
    ChargerPropertyView cpvChargerTemperature;
    @BindView(R.id.cv_cap)
    ChargerPropertyView cpvCAP;
    @BindView(R.id.cv_current)
    ChargerPropertyView cpvCurrent;
    @BindView(R.id.cv_duration)
    ChargerPropertyView cpvDuration;
    @BindView(R.id.cv_voltage)
    ChargerPropertyView cpvVoltage;
    @BindView(R.id.cv_battery_no)
    ChargerPropertyView cpvChNum;
    @BindView(R.id.vv_1)
    VoltageView vv1;
    @BindView(R.id.vv_2)
    VoltageView vv2;
    @BindView(R.id.vv_3)
    VoltageView vv3;
    @BindView(R.id.vv_4)
    VoltageView vv4;
    @BindView(R.id.vv_5)
    VoltageView vv5;
    @BindView(R.id.vv_6)
    VoltageView vv6;
    @BindView(R.id.vv_7)
    VoltageView vv7;
    @BindView(R.id.vv_8)
    VoltageView vv8;
    @BindView(R.id.vv_9)
    VoltageView vv9;
    @BindView(R.id.vv_10)
    VoltageView vv10;
    @BindView(R.id.vv_11)
    VoltageView vv11;
    @BindView(R.id.vv_12)
    VoltageView vv12;

    private UpstreamData upstreamData;
    private String deviceId;
    private int channelNum = 0;

    public static void start(Context context, UpstreamData upstreamData) {
        Intent intent = new Intent(context, ChargerDetailActivity.class);
        intent.putExtra(KEY_CHANNEL, upstreamData);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        upstreamData = (UpstreamData) getIntent().getSerializableExtra(KEY_CHANNEL);
        deviceId = upstreamData.getDownstreamData().getDeviceID();
        channelNum = upstreamData.getDownstreamData().getChannelNum();
        refreshUI(upstreamData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataManager.getInstance().addDataReceiveListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataManager.getInstance().removeDataReceiveListener(this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_charger_details;
    }


    @Override
    public void onClick(View v) {

    }

    private boolean isCurrentData(UpstreamData upstreamData) {
        return upstreamData.getDownstreamData().getChannelNum() == channelNum && upstreamData.getDownstreamData().getDeviceID().equals(deviceId);
    }

    private void refreshUI(UpstreamData upstreamData) {
        if (upstreamData != null && isCurrentData(upstreamData)) {
            tvChargerId.setText(getString(R.string.battery_, AppUIFormatUtils.getBatteryId(upstreamData)));
            tvChannel.setText(upstreamData.getDownstreamData().getChannelNum() == 0 ? R.string.ch1 : R.string.ch2);
            AppUIFormatUtils.setChargerStatusInfo(tvStatus, upstreamData);
            cpvBatteryType.setChargerProperty(AppUIFormatUtils.getBatteryType(upstreamData));
            cpvBatteryCells.setChargerProperty(upstreamData.getDownstreamData().getCells() + "");
            cpvVoltage.setChargerProperty(String.valueOf(upstreamData.getTotalVoltage()));
            cpvCurrent.setChargerProperty(String.valueOf(upstreamData.getCurrentCurrent()));
            cpvBatteryTemperature.setChargerProperty(String.valueOf(upstreamData.getBatteryTemperature()));
            cpvChargerTemperature.setChargerProperty(String.valueOf(upstreamData.getChargerTemperature()));
            cpvCAP.setChargerProperty(String.valueOf(upstreamData.getCurrentCapacity()));
            cpvDuration.setChargerProperty(upstreamData.getChargingTime());
            cpvBatteryId.setChargerProperty(AppUIFormatUtils.getBatteryId(upstreamData));
            cpvChNum.setChargerProperty(AppUIFormatUtils.getChannelNo(upstreamData));
            vv1.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[0]));
            vv2.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[1]));
            vv3.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[2]));
            vv4.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[3]));
            vv5.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[4]));
            vv6.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[5]));
            vv7.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[6]));
            vv8.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[7]));
            vv9.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[8]));
            vv10.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[9]));
            vv11.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[10]));
            vv12.setVoltageInfo(String.valueOf(upstreamData.get_BVXS()[11]));
        }
    }

    @Override
    public void onDeviceDelete() {

    }

    @Override
    public void onDataReceive(UpstreamData data) {
        refreshUI(data);
    }

    @Override
    public void onDataSendSuccess(DownstreamData data) {

    }

    @Override
    public void onDataReceiveError(String errorMsg) {

    }
}
