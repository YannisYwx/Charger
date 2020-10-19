package com.sevenchip.charger.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseRecyclerAdapter;
import com.sevenchip.charger.holder.DeviceIDHolder;

import java.util.List;

/**
 * Author : Alvin
 * CreateTime : 2020/7/26 16:54
 * Description :
 */
public class DeviceIDAdapter extends BaseRecyclerAdapter<String> {

    private DeviceIDHolder.OnItemDeleteClickListener mListener;

    public DeviceIDAdapter(List<String> dataList, DeviceIDHolder.OnItemDeleteClickListener listener) {
        super(dataList);
        this.mListener = listener;
    }

    @Override
    protected int getOtherItemType(int position) {
        return 0;
    }

    @Override
    protected RecyclerView.ViewHolder getCommonHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_id, parent, false);
        return new DeviceIDHolder(view, mListener);
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
    protected List<String> onLoadMoreData() {
        return null;
    }
}
