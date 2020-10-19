package com.sevenchip.charger.activity;

import android.view.View;
import android.widget.Button;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;

/**
 * @author : Alvin
 * create at : 2020/8/10 16:55
 * description :
 */
public class TestActivity extends BaseActivity {

    Button btnUdp;
    Button btnTcp;

    @Override
    protected void init() {
        btnUdp = findViewById(R.id.btn_udp);
        btnTcp = findViewById(R.id.btn_tcp);
        registerClickEvent(btnTcp, btnUdp);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_test_tab;
    }

    @Override
    public void onClick(View v) {
        if (v == btnTcp) {
            switchTo(TestTCPActivity.class);
        }

        if (v == btnUdp) {
            switchTo(TestUDPScanActivity.class);
        }
    }

    @Override
    public boolean isShowToolbar() {
        return false;
    }
}
