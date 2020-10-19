package com.sevenchip.charger.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.lan.LanScanDeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/8/2 16:43
 * Email : 923080261@qq.com
 * Description :
 */
public class TestUDPScanActivity extends BaseActivity implements LanScanDeviceUtils.OnScanListener {

    Button btnScan;
    Button btnStopScan;
    TextView tvLocalIp;
    TextView tvCurrentIp;
    TextView tvHint;
    ListView mListView;
    DeviceAdapter mDeviceAdapter;
    List<String> mScanDevice = new ArrayList<>();
    private Handler handler = new Handler();
    private boolean isStop = false;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isStop) {
                handler.removeCallbacks(runnable);
            } else {
                initScanHint();
                needLoopFunction();
            }
        }
    };

    private void needLoopFunction() {
        Log.e("TAG", "====" + System.currentTimeMillis());
        handler.postDelayed(runnable, 300); //延迟加载
    }

    @Override
    protected void init() {
        btnScan = findViewById(R.id.btn_scan);
        btnStopScan = findViewById(R.id.btn_scan_stop);
        tvLocalIp = findViewById(R.id.tv_local_ip);
        tvCurrentIp = findViewById(R.id.tv_device_ip_current);
        mListView = findViewById(R.id.lv_device);
        tvHint = findViewById(R.id.tv_hint);
        mDeviceAdapter = new DeviceAdapter();
        mListView.setAdapter(mDeviceAdapter);

        tvLocalIp.setText("");
        tvCurrentIp.setText("");
        LanScanDeviceUtils.getInstance().setListener(this);

    }

    @Override
    protected void initEvent() {
        btnScan.setOnClickListener(view -> {
            mScanDevice.clear();
            handler.postDelayed(runnable, 300);
            LanScanDeviceUtils.getInstance().scan();
            btnScan.setEnabled(false);
            mDeviceAdapter.notifyDataSetChanged();
        });
        btnStopScan.setVisibility(View.GONE);
        btnStopScan.setOnClickListener(view -> {
            isStop = true;
            tvHint.setText("搜索已停止");
            LanScanDeviceUtils.getInstance().stopScan();
        });
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onScanHostAddress(String address) {
        tvLocalIp.setText("本机ip = " + address);
        initScanHint();
    }

    @Override
    public void onScanError() {
        tvHint.setText("搜索失败。请检查是否连接WIFI");
        isStop = true;
        initScanHint();
    }

    @Override
    public void onScanSuccess(String address, int code) {
        tvCurrentIp.setText("当前扫描到的ip:" + address + (code == 0 ? "-已扫描到" : "-不存在"));
        initScanHint();
        if(!mScanDevice.contains(address)) {
            mScanDevice.add(address);
            mDeviceAdapter.notifyDataSetChanged();
        }
    }

    private void initScanHint() {
        String tv = tvHint.getText().toString();
        String suffix = ".";
        if (tv.contains("...")) {
            suffix = ".";
        } else if (tv.contains("..")) {
            suffix = "...";
        } else if (tv.contains(".")) {
            suffix = "..";
        }
        tvHint.setText("正在搜索" + suffix);
    }

    @Override
    public void onScanAddressFail(String address, int code) {
        tvCurrentIp.setText("当前扫描到的ip:" + address + (code == 0 ? "-已扫描到" : "-不存在"));
    }

    @Override
    public void onScanFinished(List<String> devList) {
        tvHint.setText("搜索结束。");
        tvCurrentIp.setText("------------------------------------");
        isStop = true;
        btnScan.setEnabled(true);
    }

    @Override
    public void onScanStop() {
        isStop = true;
        btnScan.setEnabled(true);
    }

    private class DeviceAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public DeviceAdapter() {
            mInflater = LayoutInflater.from(TestUDPScanActivity.this);
        }

        @Override
        public int getCount() {
            return mScanDevice.size();
        }

        @Override
        public Object getItem(int i) {
            return mScanDevice.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view = mInflater.inflate(R.layout.item_device, null);
                viewHolder = new ViewHolder();
                viewHolder.tvIp = view.findViewById(R.id.tv_ip);
                viewHolder.btnTest = view.findViewById(R.id.btn_connect);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tvIp.setText(mScanDevice.get(i));
            viewHolder.btnTest.setOnClickListener(view1 -> {
                Bundle bundle = new Bundle();
                bundle.putString("IP", mScanDevice.get(i));
                switchTo(bundle, TestUDPConnActivity.class);
            });

            return view;
        }

        class ViewHolder {
            TextView tvIp;
            Button btnTest;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            LanScanDeviceUtils.getInstance().kill();
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean isShowToolbar() {
        return false;
    }
}
