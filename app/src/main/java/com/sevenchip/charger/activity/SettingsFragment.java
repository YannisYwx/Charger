package com.sevenchip.charger.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseMVPFragment;
import com.sevenchip.charger.contract.SettingsContract;
import com.sevenchip.charger.data.bean.BatteryCharger;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.status.BatteryType;
import com.sevenchip.charger.data.status.WorkMode;
import com.sevenchip.charger.data.status.WorkStatus;
import com.sevenchip.charger.presenter.SettingsPresenter;
import com.sevenchip.charger.utils.AppUIFormatUtils;
import com.sevenchip.charger.utils.PrefUtils;
import com.sevenchip.charger.widget.SettingPropertyView;
import com.sevenchip.charger.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : Alvin
 * create at : 2020/7/29 13:03
 * description :
 */
public class SettingsFragment extends BaseMVPFragment<SettingsPresenter> implements SettingsContract.View {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private static final String KEY_CHANNEL_NUM = "_KEY_CHANNEL_NUM";
    private static final String KEY_BATTERY_CHARGER = "_KEY_BATTERY_CHARGER";

    @BindView(R.id.rg_battery_type)
    RadioGroup rgBatteryType;
    @BindView(R.id.rg_charging_mode)
    RadioGroup rgChargerMode;
    @BindView(R.id.spv_current)
    SettingPropertyView spvCurrent;
    @BindView(R.id.spv_maximum_capacity)
    SettingPropertyView spvCapacity;
    @BindView(R.id.spv_maximum_time)
    SettingPropertyView spvChargerTime;
    @BindView(R.id.spv_temperature_alarm)
    SettingPropertyView spvTemperature;
    @BindView(R.id.sb_status)
    SwitchButton sbStatus;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.btn_setting)
    Button btnSend;

    private DownstreamData mDownstreamData = null;
    private UpstreamData upstreamData = null;
    private BatteryCharger mBatteryCharger = null;
    private int channelNum = 0;

    private float upkeepValue = -1f;
    private float chargingValue = -1f;
    private float dischargeValue = -1f;
    private float balanceValue = -1f;
    private float currentCurrent = 0.0f;
    private String deviceID;
    private int maxChargingTime;
    private int maxBatteryCapacity;
    private float temperature;

    private int wokeMode = -1;
    private int batteryType = -1;

    public static SettingsFragment getInstance(int channelNum, BatteryCharger batteryCharger) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CHANNEL_NUM, channelNum);
        args.putSerializable(KEY_BATTERY_CHARGER, batteryCharger);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView(View rootView, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
        Bundle args = getArguments();
        if (args != null) {
            mBatteryCharger = (BatteryCharger) args.getSerializable(KEY_BATTERY_CHARGER);
            channelNum = args.getInt(KEY_CHANNEL_NUM);
            if (mBatteryCharger != null) {
                upstreamData = channelNum == 0 ? mBatteryCharger.getCH01() : mBatteryCharger.getCH02();
                if (upstreamData != null) {
                    //获取当前的工作模式 并保存状态
                    mDownstreamData = upstreamData.getDownstreamData();
                    if (mDownstreamData != null) {
                        String saveData = getSaveData(mDownstreamData);
                        Log.e(TAG, "Get Data = " + saveData);
                        if (!TextUtils.isEmpty(saveData)) {
                            initViewWithSaveData(saveData);
                        } else {
                            initViewWithDefaultData();
                        }
                    } else {
                        btnSend.setEnabled(false);
                    }
                    //判断当前的辅助状态 如果是异常状态则强制停止 且不可更改状态
                    int status = AppUIFormatUtils.getChargerWorkStatus(upstreamData);
                    ChargerSettingsActivity act = (ChargerSettingsActivity) getActivity();
                    if (status == WorkStatus.Error) {
                        sbStatus.setChecked(false);
                        sbStatus.setEnabled(false);
                        sbStatus.setAlpha(0.5f);
                        if (act != null) {
                            act.showWornAppMsg();
                        }
                    } else {
                        // 0 : stop 1 : start
                        sbStatus.setChecked(mDownstreamData.getStatus() == 0);
                        sbStatus.setEnabled(true);
                        if (act != null) {
                            act.dismissAllMsg();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setPresenter(new SettingsPresenter(this));
        presenter.onAttach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }

    private void setCurrentValue() {
        float value = spvCurrent.getValue() / 10.0f;
        switch (wokeMode) {
            case WorkMode.Upkeep:
                upkeepValue = value;
                break;
            case WorkMode.Charging:
                chargingValue = value;
                break;
            case WorkMode.Discharging:
                dischargeValue = value;
                break;
            case WorkMode.Balance:
                balanceValue = value;
                break;
            default:
                break;
        }
    }

    @Override
    public void initEvent() {
        rgBatteryType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_LiPo:
                    batteryType = BatteryType.LiPo;
                    break;
                case R.id.rb_LiHv:
                    batteryType = BatteryType.LiHv;
                    break;
                case R.id.rb_FULLY_MAX:
                    batteryType = BatteryType.FULLY_MAX;
                    break;
                case R.id.rb_GENS_ACE:
                    batteryType = BatteryType.GENS_ACE;
                    break;
                default:
                    break;
            }
        });

        rgChargerMode.setOnCheckedChangeListener((group, checkedId) -> {
            setCurrentValue();
            int min, max;
            String hint, highlight, value;
            wokeMode = WorkMode.Upkeep;
            switch (checkedId) {
                case R.id.rb_upkeep:
                    wokeMode = WorkMode.Upkeep;
                    min = 5;
                    max = 25;
                    value = String.valueOf(upkeepValue);
                    break;
                case R.id.rb_charging:
                    wokeMode = WorkMode.Charging;
                    min = 5;
                    max = 25;
                    value = String.valueOf(chargingValue);
                    break;
                case R.id.rb_discharge:
                    wokeMode = WorkMode.Discharging;
                    min = 1;
                    max = 5;
                    value = String.valueOf(dischargeValue);
                    break;
                case R.id.rb_balance:
                    wokeMode = WorkMode.Balance;
                    min = 1;
                    max = 3;
                    value = String.valueOf(balanceValue);
                    break;
                default:
                    min = 5;
                    max = 25;
                    value = String.valueOf(upkeepValue);
                    break;
            }
            highlight = group.getContext().getResources().getString(R.string.spv_highlight, min, max, "A");
            hint = group.getContext().getResources().getString(R.string.spv_hint, highlight);
            spvCurrent.setChargerProperty(value, hint, highlight, min, max);
        });
    }

    @Override
    protected int setContentLayout() {
        return R.layout.fragmet_settings;
    }

    private void initViewWithDefaultData() {
        btnSend.setEnabled(mDownstreamData.getStatus() != 0);
        rgBatteryType.check(getCheckBatteryTypeId(BatteryType.LiPo));
        rgChargerMode.check(getCheckWorkModeId(WorkMode.Charging));
        spvCurrent.setChargerProperty(0.1f, 25f, "5");
        spvTemperature.setChargerProperty(40f, 80f, "80");
        spvChargerTime.setChargerProperty(10f, 120f, "120");
        spvCapacity.setChargerProperty(1000f, 100000f, "100000");
    }

    /**
     * 通过保存的数据初始化界面
     */
    private void initViewWithSaveData(@NonNull String data) {
        String[] dataArray = data.split(SPLIT);
        deviceID = dataArray[0];
        channelNum = Integer.parseInt(dataArray[1]);
        batteryType = Integer.parseInt(dataArray[2]);
        wokeMode = Integer.parseInt(dataArray[3]);
        currentCurrent = Float.parseFloat(dataArray[4]);
        temperature = Integer.parseInt(dataArray[5]);
        maxChargingTime = Integer.parseInt(dataArray[6]);
        maxBatteryCapacity = Integer.parseInt(dataArray[7]);
        switch (batteryType) {
            case BatteryType.LiPo:
                rgBatteryType.check(R.id.rb_LiPo);
                break;
            case BatteryType.LiHv:
                rgBatteryType.check(R.id.rb_LiHv);
                break;
            case BatteryType.FULLY_MAX:
                rgBatteryType.check(R.id.rb_FULLY_MAX);
                break;
            case BatteryType.GENS_ACE:
                rgBatteryType.check(R.id.rb_GENS_ACE);
                break;
        }
        //设置电流
        int min, max;
        String hint, highlight, value;
        switch (wokeMode) {
            case WorkMode.Charging:
                min = 5;
                max = 25;
                chargingValue = currentCurrent;
                value = String.valueOf(chargingValue);
                rgChargerMode.check(R.id.rb_charging);
                break;
            case WorkMode.Discharging:
                min = 1;
                max = 5;
                dischargeValue = currentCurrent;
                value = String.valueOf(dischargeValue);
                rgChargerMode.check(R.id.rb_discharge);
                break;
            case WorkMode.Balance:
                min = 1;
                max = 3;
                balanceValue = currentCurrent;
                value = String.valueOf(balanceValue);
                rgChargerMode.check(R.id.rb_balance);
                break;
            default:
                min = 5;
                max = 25;
                upkeepValue = currentCurrent;
                value = String.valueOf(upkeepValue);
                rgChargerMode.check(R.id.rb_upkeep);
                break;
        }
        highlight = mContext.getResources().getString(R.string.spv_highlight, min, max, "A");
        hint = mContext.getResources().getString(R.string.spv_hint, highlight);
        spvCurrent.setChargerProperty(value, hint, highlight, min, max);
        temperature = Math.max(40, temperature);
        maxChargingTime = Math.max(10, maxChargingTime);
        maxBatteryCapacity = Math.max(1000, maxBatteryCapacity);
        spvTemperature.setChargerProperty(40f, 80f, String.valueOf(temperature));
        spvChargerTime.setChargerProperty(10f, 120f, String.valueOf(maxChargingTime));
        spvCapacity.setChargerProperty(1000f, 100000f, String.valueOf(maxBatteryCapacity));
    }

    private void sendData() {
        int batteryType = AppUIFormatUtils.getData_BatteryType(rgBatteryType);
        int workMode = AppUIFormatUtils.getData_WorkModeData(rgChargerMode);
        float current = spvCurrent.getValue() / 10.0f;
        int temperature = spvTemperature.getValue();
        int chargerTime = spvChargerTime.getValue();
        int capacity = spvCapacity.getValue();
        int channelNum = mDownstreamData.getChannelNum();
        int status = sbStatus.isChecked() ? 0 : 1;
        String deviceId = mDownstreamData.getDeviceID();
        DownstreamData downstreamData = DownstreamData.createBuilder()
                .batteryType(batteryType)
                .cells(12)
                .channelNum(channelNum)
                .maxBatteryCapacity(capacity)
                .workMode(workMode)
                .temperatureAlarm(temperature)
                .maxChargingTime(chargerTime)
                .status(status)
                .current(current)
                .deviceID(deviceId)
                .build();
        Log.d(TAG, downstreamData.toString());
        presenter.applySettings(downstreamData);
    }


    private int getCheckBatteryTypeId(int batteryType) {
        switch (batteryType) {
            case BatteryType.LiHv:
                return R.id.rb_LiHv;
            case BatteryType.FULLY_MAX:
                return R.id.rb_FULLY_MAX;
            case BatteryType.GENS_ACE:
                return R.id.rb_GENS_ACE;
            default:
                return R.id.rb_LiPo;
        }
    }

    private int getCheckWorkModeId(int workMode) {
        switch (workMode) {
            case WorkMode.Charging:
                return R.id.rb_charging;
            case WorkMode.Discharging:
                return R.id.rb_discharge;
            case WorkMode.Balance:
                return R.id.rb_balance;
            default:
                return R.id.rb_upkeep;
        }
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
        if (mDownstreamData != null) {
            //保存临时数据
            //当前电流
            setCurrentValue();
            temperature = spvTemperature.getValue();
            maxChargingTime = spvChargerTime.getValue();
            maxBatteryCapacity = spvCapacity.getValue();
        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void applySettingsResult(boolean isOk) {
        showToast(isOk ? R.string.settings_success : R.string.settings_failed);
    }

    @Override
    public void sendSuccess() {
        showToast(R.string.data_send_success);
    }

    @OnClick(R.id.btn_setting)
    public void send() {
        saveData(mDownstreamData);
        sendData();
    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    private static final String SPLIT = "@";

    public void saveData(DownstreamData downstreamData) {
        StringBuilder sb = new StringBuilder();
        sb.append(downstreamData.getDeviceID())//0
                .append(SPLIT)
                .append(downstreamData.getChannelNum())//1
                .append(SPLIT)
                .append(batteryType)//2
                .append(SPLIT)
                .append(wokeMode)//3
                .append(SPLIT)
                .append(spvCurrent.getValue() / 10.0f)//4
                .append(SPLIT)
                .append(spvTemperature.getValue())//5
                .append(SPLIT)
                .append(spvChargerTime.getValue())//6
                .append(SPLIT)
                .append(spvCapacity.getValue())//7
                .append(SPLIT);

        String data = sb.toString();
        Log.e(TAG, "Save Data = " + data);
        PrefUtils.saveStringToPrefs(mContext, getKey(downstreamData), data);
    }

    public String getSaveData(DownstreamData downstreamData) {
        return PrefUtils.getStringFromPrefs(mContext, getKey(downstreamData), null);
    }

    public static String getKey(DownstreamData downstreamData) {
        if (downstreamData != null) {
            return downstreamData.getDeviceID() + SPLIT + downstreamData.getChannelNum();
        } else {
            return "";
        }
    }
}
