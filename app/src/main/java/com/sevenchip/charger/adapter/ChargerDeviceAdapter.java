package com.sevenchip.charger.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseRecyclerAdapter;
import com.sevenchip.charger.data.bean.BatteryCharger;
import com.sevenchip.charger.holder.ChargerDeviceHolder;

import java.util.List;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/26 16:54
 * Email : 923080261@qq.com
 * Description :
 */
public class ChargerDeviceAdapter extends BaseRecyclerAdapter<BatteryCharger> {

    public ChargerDeviceAdapter(List<BatteryCharger> dataList, ChargerDeviceHolder.OnBatteryStatusChangeListener listener) {
        super(dataList);
        this.mListener = listener;
    }

    ChargerDeviceHolder.OnBatteryStatusChangeListener mListener;

    public void setListener(ChargerDeviceHolder.OnBatteryStatusChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    protected int getOtherItemType(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder getCommonHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charger, parent, false);
        return new ChargerDeviceHolder(view, mListener);
    }

    @Override
    protected RecyclerView.ViewHolder getSpecialHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected int initOtherItemCount() {
        return 0;
    }

    @Override
    protected boolean hasLoadMore() {
        return false;
    }

    @Override
    protected List<BatteryCharger> onLoadMoreData() throws Exception {
        return null;
    }
}
