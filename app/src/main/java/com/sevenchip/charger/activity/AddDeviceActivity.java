package com.sevenchip.charger.activity;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.utils.ByteUtils;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

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

    @BindView(R.id.tv_ids)
    TextView tvIds;
    @BindView(R.id.et_number)
    EditText etNumber;

    String deviceID;
    @BindView(R.id.sp_num)
    Spinner mSpinner;

    ArrayAdapter<String> mAdapter;

    @Override
    protected void init() {
    }

    @Override
    protected void initEvent() {
        registerClickEvent(btnAdd);
    }

    @Override
    public void initData() {
        super.initData();
        for (int i = 1; i < 51; i++) {
            numbers.add("" + (i));
        }
        mAdapter = new ArrayAdapter<>(this, R.layout.item_num, numbers);
        //设置下拉框的标题，不设置就没有难看的标题了
        mSpinner.setPrompt("请选择行星");
        //设置下拉框的数组适配器
        mSpinner.setAdapter(mAdapter);
        //设置下拉框默认的显示第一项
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("===================beforeTextChanged：" + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("===================onTextChanged：" + s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("===================afterTextChanged：" + s);
                sss();
            }
        });

    }

    private List<String> numbers = new ArrayList<>();
    private List<String> ids = new ArrayList<>();

    private void sss() {
        String strID = etNumber.getText().toString();
        int le = strID.length();
        int c = 6 - le;
        StringBuilder k = new StringBuilder();
        StringBuilder sbb = new StringBuilder();
        for (int j = 0; j < c; j++) {
            k.append("0");
        }
        String u = k.toString() + strID;
        byte[] b = ByteUtils.string2ByteArray(u);
        int startId = ByteUtils.byteArray2Int(b, 0, b.length, false);
        int pos = mSpinner.getSelectedItemPosition();
        pos++;
        for (int i = 0; i < pos; i++) {
            byte[] sb = ByteUtils.int2ByteArray(startId + i, ByteOrder.BIG_ENDIAN);
            String id1 = ByteUtils.byteArray2String(sb);
            String id2 = id1.substring(3);
            System.out.println(id2 + "********************************" + id1 + "----" + u + "^^^^^^^^^^^^^^^^^" + startId);
            ids.add(id2);
            sbb.append(id2).append("~");
        }
        tvIds.setText(sbb.toString());


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

            StringBuilder sb = new StringBuilder();
            for(String id:ids){
                sb.append(id).append("|");
            }


            boolean isSave = DataManager.getInstance().saveDevice(this, sb.toString());
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
