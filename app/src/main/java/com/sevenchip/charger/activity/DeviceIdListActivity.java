package com.sevenchip.charger.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.adapter.DeviceIDAdapter;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.holder.DeviceIDHolder;
import com.sevenchip.charger.utils.DialogUtils;
import com.sevenchip.charger.utils.RecyclerViewSpacesItemDecoration;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Author : Alvin
 * CreateTime : 2020/8/15 23:07
 * Description :
 */
public class DeviceIdListActivity extends BaseActivity implements DeviceIDHolder.OnItemDeleteClickListener {


    @BindView(R.id.rv_device_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    DeviceIDAdapter mAdapter;
    private List<String> deviceList;

    @Override
    protected void init() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        //右间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);
        //右间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);

        deviceList = DataManager.getInstance().getAllBindDeviceIds(this);

        if (deviceList.size() > 0) {
            mAdapter = new DeviceIDAdapter(deviceList, this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(DeviceIdListActivity.this));
            mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_device_list;
    }

    @Override
    public void onItemDelete(String deviceId) {
        DialogUtils.showMessageDialog(this, getString(R.string.dialog_title_delete_device), deviceId, new DialogUtils.OnDialogClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure() {
                boolean isSuccess = DataManager.getInstance().deleteDevice(DeviceIdListActivity.this, deviceId);
                showToast(isSuccess ? "删除成功" : "删除失败");
                deviceList.remove(deviceId);
                mAdapter.notifyDataSetChanged();
                tvEmpty.setVisibility(deviceList.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }
}
