package com.sevenchip.charger.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sevenchip.charger.data.bean.BatteryCharger;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;


/**
 * @author : Alvin
 * createTime : 2017/10/12  17:58
 * description :
 */
public class SettingsPagerAdapter extends FragmentStatePagerAdapter {
    private String[] titles;

    private BatteryCharger mBatteryCharger;

    public SettingsPagerAdapter(@NonNull String[] titles, BatteryCharger batteryCharger, FragmentManager fm) {
        super(fm);
        this.mBatteryCharger = batteryCharger;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return SettingsFragment.getInstance(position, mBatteryCharger);
    }

    private DownstreamData getDownstreamData(int position) {
        if (mBatteryCharger == null) return null;
        UpstreamData ch1 = mBatteryCharger.getCH01();
        UpstreamData ch2 = mBatteryCharger.getCH02();
        DownstreamData d1 = null;
        DownstreamData d2 = null;
        if (ch1 != null) {
            d1 = ch1.getDownstreamData();
        }
        if (ch2 != null) {
            d2 = ch2.getDownstreamData();
        }
        return position == 0 ? d1 : d2;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null) {
            return titles[position];
        }
        return super.getPageTitle(position);
    }
}
