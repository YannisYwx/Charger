package com.sevenchip.charger.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.lan.udp.TCPManager;
import com.sevenchip.charger.utils.ByteUtils;
import com.sevenchip.charger.utils.NetworkUtils;

/**
 * @author : Alvin
 * create at : 2020/8/10 17:02
 * description :
 */
public class TestTCPActivity extends BaseActivity implements TCPManager.TCPConnectListener {
    private static final String TAG = TestTCPActivity.class.getSimpleName();
    Button btnScan;
    Button btnConnect;
    Button btnApply;

    TextView tvIP;
    TextView tvTitle;
    TextView tvContent;
    EditText etSSID;
    EditText etPassword;

    String hotspotAddress;
    String localAddress;

    private String ssid;
    private String pwd;

    @Override
    protected void init() {
        btnScan = findViewById(R.id.btn_scan);
        btnConnect = findViewById(R.id.btn_connect);
        btnApply = findViewById(R.id.btn_apply);
        tvIP = findViewById(R.id.tv_device_ip);
        etSSID = findViewById(R.id.et_ssid);
        etPassword = findViewById(R.id.et_password);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_gg);
        hotspotAddress = NetworkUtils.getHotspotAddress(this);
        localAddress = NetworkUtils.getLocalAddress(this);
        if (!TextUtils.isEmpty(hotspotAddress)) {
            tvIP.setText(String.format("热点Ip:%s", hotspotAddress));
        } else {
            showToast("请检查是否连接wifi");
        }
        if (!TextUtils.isEmpty(localAddress)) {
            tvTitle.setText(localAddress);
        }
        TCPManager.getInstance().setListener(this);
    }

    @Override
    protected void initEvent() {
        registerClickEvent(btnApply, btnConnect, btnScan);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_test_tcp;
    }

    @Override
    public void onClick(View v) {
        if (v == btnApply) {
            ssid = etSSID.getText().toString().trim();
            pwd = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                showToast("账号和密码都不能为空");
            } else {
                if (TCPManager.getInstance().isConnect()) {
                    String message = "ssid@" + ssid + ",pswd@" + pwd;
                    TCPManager.getInstance().sendMessage(message);
                    tvContent.setText(String.format("发送内容：%s", ByteUtils.byteArray2HexStringWithSpaces(message.getBytes(), message.getBytes().length)));
                } else {
                    showToast("请先连接设备");
                }
            }

        }

        if (v == btnConnect) {
            if (TCPManager.getInstance().isConnect()) {
                showToast("已连接");
            } else {
                TCPManager.getInstance().connect(hotspotAddress, 5050);
            }
        }

        if (v == btnScan) {
            hotspotAddress = NetworkUtils.getHotspotAddress(this);
            localAddress = NetworkUtils.getLocalAddress(this);
            if (!TextUtils.isEmpty(hotspotAddress)) {
                tvIP.setText(String.format("热点Ip:%s", hotspotAddress));
            } else {
                showToast("请检查是否连接wifi");
            }
        }
    }

    @Override
    public void onTCPConnect(String ip) {
        showToast("设备已连接");
        tvTitle.setText(String.format("%s已连接", localAddress));
    }

    @Override
    public void onTCPConnectError(String ip, String errorMsg) {
        Log.d(TAG, "设备连接失败：" + errorMsg);
        showToast("设备连接失败");
        tvTitle.setText(String.format("%s未连接", localAddress));
    }

    @Override
    public void onMessageSendComplete(String ip) {
        showToast("数据发送完成：" + ip);
    }

    @Override
    public void onMessageSendError(String ip, String errorMsg) {
        Log.d(TAG, "数据发送失败：" + ip + " ErrorMsg = " + errorMsg);
        showToast("数据发送失败：" + ip);
    }

    @Override
    public boolean isShowToolbar() {
        return false;
    }
}
