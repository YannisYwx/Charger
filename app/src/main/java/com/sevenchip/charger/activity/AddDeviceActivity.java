package com.sevenchip.charger.activity;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.DataManager;

import butterknife.BindView;

/**
 * Author : Alvin
 * CreateTime : 2020/8/13 22:52
 * Description :
 */
public class AddDeviceActivity extends BaseActivity {
    private static final String TAG = AddDeviceActivity.class.getSimpleName();

    @BindView(R.id.btn_add)
    TextView btnAdd;
    @BindView(R.id.et_number)
    EditText etNumber;

    String deviceID;

    @Override
    protected void init() {
    }

    @Override
    protected void initEvent() {
        registerClickEvent(btnAdd);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_add_device;
    }

    @Override
    public void onClick(View v) {
        if (v == btnAdd) {
            deviceID = etNumber.getText().toString();
            if (TextUtils.isEmpty(deviceID)) {
                showToast("设备id不能为空");
                return;
            }
            if (deviceID.length() != 5) {
                showToast("设备id长度必须为5");
                return;
            }
            boolean isSave = DataManager.getInstance().saveDevice(this, deviceID);
            if (isSave) {
                showToast("设备添加成功");
            } else {
                showToast("设备已存在");
            }
        }
    }

    @Override
    public void onRightTitleBarClick(@NonNull View view) {
        super.onRightTitleBarClick(view);
        switchTo(DeviceIdListActivity.class);
    }
}
