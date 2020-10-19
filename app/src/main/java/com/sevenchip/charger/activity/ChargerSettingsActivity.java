package com.sevenchip.charger.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseMVPActivity;
import com.sevenchip.charger.contract.SettingsContract;
import com.sevenchip.charger.data.bean.BatteryCharger;
import com.sevenchip.charger.presenter.SettingsPresenter;
import com.sevenchip.charger.utils.MagicIndicatorUtils;
import com.sevenchip.charger.utils.UIUtils;
import com.sevenchip.magicindicator.MagicIndicator;

import butterknife.BindView;

/**
 * @author : Alvin
 * create at : 2020/7/29 12:52
 * description :
 */
public class ChargerSettingsActivity extends BaseMVPActivity<SettingsPresenter> implements SettingsContract.View {

    public static final String KEY_BATTERY_CHARGER = "_KEY_BATTERY_CHARGER";

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.magicIndicator)
    MagicIndicator mMagicIndicator;
    SettingsPagerAdapter mAdapter;
    BatteryCharger mBatteryCharger;

    public static void start(Context context, BatteryCharger batteryCharger) {
        Intent intent = new Intent(context, ChargerSettingsActivity.class);
        intent.putExtra(KEY_BATTERY_CHARGER, batteryCharger);
        context.startActivity(intent);
    }

    @Override
    public SettingsPresenter createPresenter() {
        return new SettingsPresenter(this);
    }

    @Override
    protected void init() {
        UIUtils.init(this);
        mBatteryCharger = (BatteryCharger) getIntent().getSerializableExtra(KEY_BATTERY_CHARGER);
        mAdapter = new SettingsPagerAdapter(getResources().getStringArray(R.array.channels), mBatteryCharger, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        MagicIndicatorUtils.bindViewpager(mMagicIndicator, this, mViewPager, getResources().getStringArray(R.array.channels));
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_charger_settings;
    }

    @Override
    public void applySettingsResult(boolean isOk) {

    }

    @Override
    public void sendSuccess() {

    }

    @Override
    protected void onPause() {
        dismissAllMsg();
        super.onPause();
    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }
}
