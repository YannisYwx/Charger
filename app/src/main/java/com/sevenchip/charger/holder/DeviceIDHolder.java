package com.sevenchip.charger.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseRecycleHolder;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/26 17:45
 * Email : 923080261@qq.com
 * Description :
 */
public class DeviceIDHolder extends BaseRecycleHolder<String> {
    @BindView(R.id.tv_device_id)
    TextView tvDeviceId;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.view_item_device_id)
    View rootView;

    public DeviceIDHolder(@NonNull View itemView, OnItemDeleteClickListener listener) {
        super(itemView);
        this.listener = listener;
    }

    public OnItemDeleteClickListener listener;

    @Override
    protected void refreshViewHolder(String deviceId) {
        tvDeviceId.setText(deviceId);
        rootView.setOnClickListener(null);
    }

    @OnClick(R.id.iv_delete)
    public void onDeleteDevice() {
        if (listener != null) {
            listener.onItemDelete(getData());
        }

    }

    public interface OnItemDeleteClickListener {
        void onItemDelete(String deviceId);
    }
}
