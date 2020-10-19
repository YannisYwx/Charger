package com.sevenchip.charger.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.data.lan.udp.TCPManager;
import com.sevenchip.charger.utils.NetworkUtils;

import butterknife.BindView;

/**
 * Author : Alvin
 * CreateTime : 2020/8/16 22:09
 * Description :
 */
public class WifiSettingActivity extends BaseActivity implements TCPManager.TCPConnectListener {
    private static final String TAG = WifiSettingActivity.class.getSimpleName();
    @BindView(R.id.et_ssid)
    EditText etSSID;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_hotspot)
    TextView tvIP;
    @BindView(R.id.btn_apply)
    Button btnApply;

    private String hotspotAddress;
    private String localAddress;
    private String ssid;
    private String pwd;

    @Override
    protected void init() {
        hotspotAddress = NetworkUtils.getHotspotAddress(this);
        localAddress = NetworkUtils.getLocalAddress(this);
        if (!TextUtils.isEmpty(hotspotAddress)) {
            tvIP.setText(hotspotAddress);
        } else {
            showToast(getString(R.string.check_wifi_status));
            btnApply.setEnabled(false);
            tvIP.setText(R.string.check_wifi_status);
            etPassword.setEnabled(false);
            etSSID.setEnabled(false);
        }
        TCPManager.getInstance().setListener(this);
    }

    @Override
    protected void initEvent() {
        registerClickEvent(btnApply);
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_wifi_settings;
    }

    @Override
    public void onClick(View v) {
        if (v == btnApply) {
            ssid = etSSID.getText().toString().trim();
            pwd = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                showToast("account and password can not be null");
            } else {
//                String message = "ssid@" + ssid + ",pswd@" + pwd;
//                TCPManager.getInstance().sendMessage(hotspotAddress, 5050, message);
                DataManager.getInstance().settingWifiAccount(hotspotAddress, ssid, pwd);
            }
        }
    }

    @Override
    public void onTCPConnect(String ip) {
        showToast("device is connect");
    }

    @Override
    public void onTCPConnectError(String ip, String errorMsg) {
        showToast("device connect error");
    }

    @Override
    public void onMessageSendComplete(String ip) {
        showToast("data send success ：" + ip);
    }

    @Override
    public void onMessageSendError(String ip, String errorMsg) {
        showToast("data send fail ：" + ip);
    }
}
