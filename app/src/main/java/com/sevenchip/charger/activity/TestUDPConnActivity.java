package com.sevenchip.charger.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseActivity;
import com.sevenchip.charger.data.TestDataMode;
import com.sevenchip.charger.data.lan.udp.UDPManager;
import com.sevenchip.charger.utils.ByteUtils;

/**
 * @author : Alvin
 * create at : 2020/8/6 13:08
 * description :
 */
public class TestUDPConnActivity extends BaseActivity implements UDPManager.OnLanMessageListener {
    public static final String TAG = TestUDPConnActivity.class.getSimpleName();
    TextView tvTitle;
    TextView tvContent;
    TextView tvContentSend;
    TextView tvReceive;
    EditText etLocalPort;
    EditText etDevicePort;
    EditText etContent;
    Button btnSend;
    Button btnReceive;
    Button btnStopReceive;
    Button btnSendDefault;
    private String ip;
    private int localPort = 4000;
    private int devicePort = 5000;
    private String content;

//    UDPSocket mUDPSocket;

    @Override
    protected void init() {
        Bundle bundle = getIntent().getBundleExtra(EXTRA);
        ip = bundle.getString("IP");
        tvTitle = findViewById(R.id.tv_ip);
        tvReceive = findViewById(R.id.tv_re);
        etLocalPort = findViewById(R.id.et_port_local);
        etDevicePort = findViewById(R.id.et_port_device);
        etContent = findViewById(R.id.et_content);
        btnSend = findViewById(R.id.btn_send);
        btnReceive = findViewById(R.id.btn_start_receive);
        btnStopReceive = findViewById(R.id.btn_stop_receive);
        btnSendDefault = findViewById(R.id.btn_send_default);
        tvContent = findViewById(R.id.tv_content);
        tvContentSend = findViewById(R.id.tv_gg);
        registerClickEvent(btnReceive, btnSend, btnStopReceive, btnSendDefault);
        tvTitle.setText(TextUtils.isEmpty(ip) ? "UnKnow address" : ip);
//        mUDPSocket = new UDPSocket();
//        mUDPSocket.setOnLanMessageListener(this);

        UDPManager.getInstance().addLanMessageListener(this);
        UDPManager.getInstance().setIpPort(ip, devicePort, localPort);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_test_conn;
    }

    @Override
    public void showToast(String msg) {
        msg += UDPManager.getInstance().listenerHostInfo();
        super.showToast(msg);
    }

    @Override
    public void onClick(View v) {
        String strDevicePort = etDevicePort.getText().toString();
        if (TextUtils.isEmpty(strDevicePort)) {
            devicePort = 5000;
        } else {
            devicePort = Integer.parseInt(strDevicePort);
        }
        String strLocalPort = etLocalPort.getText().toString();
        if (TextUtils.isEmpty(strLocalPort)) {
            localPort = 4000;
        } else {
            localPort = Integer.parseInt(strLocalPort);
        }
        UDPManager.getInstance().setIpPort(ip, devicePort, localPort);
        if (v.getId() == R.id.btn_start_receive) {
            if (!UDPManager.getInstance().isReceiveRunning()) {
                UDPManager.getInstance().startListenerBroadcast();
                showToast("开始监听");
            } else {
                showToast("请不要重复开启监听");
            }
        }

        if (v.getId() == R.id.btn_stop_receive) {

            if (UDPManager.getInstance().isReceiveRunning()) {
                UDPManager.getInstance().stopListenerBroadcast();
                showToast("停止监听");
            } else {
                showToast("当前已经没有监听！");
            }
        }

        if (v.getId() == R.id.btn_send) {
            String msg = etContent.getText().toString();
            if (!TextUtils.isEmpty(msg)) {
                showToast("开始发送");
                UDPManager.getInstance().sendMessage(msg);
                Log.d(TAG, "发送数据：data = " + ByteUtils.byteArray2HexStringWithSpaces(msg.getBytes(), msg.getBytes().length));
            } else {
                showToast("发送内容不能为空");
            }
        }
        if (v.getId() == R.id.btn_send_default) {
            showToast("开始固定格式数据发送");
            UDPManager.getInstance().sendMessage(TestDataMode.getDownstreamData());
        }
    }

    @Override
    public void onDataReceive(byte[] data) {
        showToast("收到数据");
        String strReceive = new String(data, 0, data.length);
        tvReceive.setText(strReceive);
    }

    @Override
    public void onDataSendComplete(byte[] data) {
        showToast("数据发送成功");
        tvContentSend.setText("数据发送成功：" + ByteUtils.byteArray2HexStringWithSpaces(data, data.length));
    }

    @Override
    public void onDataSendError(byte[] data, String errorMsg) {
        showToast("数据发送失败");
        Log.d(TAG, "数据发送失败：data = " + ByteUtils.byteArray2HexStringWithSpaces(data, data.length));
        Log.d(TAG, "数据发送失败：msg = " + errorMsg);
    }

    @Override
    public void onDataReceiveError(String errorMsg) {
        showToast("数据接受失败");
        Log.d(TAG, "数据接受失败：msg = " + errorMsg);
    }

    @Override
    protected void onDestroy() {
//        try {
//            mUDPSocket.stopUDPSocket();
//            mUDPSocket = null;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        UDPManager.getInstance().removeLanMessageListener(this);
        UDPManager.getInstance().stop();
        super.onDestroy();
    }

    @Override
    public boolean isShowToolbar() {
        return false;
    }
}
