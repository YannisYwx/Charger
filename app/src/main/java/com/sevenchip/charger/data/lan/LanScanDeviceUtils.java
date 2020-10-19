package com.sevenchip.charger.data.lan;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sevenchip.charger.data.lan.executor.ThreadExecutor;
import com.sevenchip.charger.data.lan.executor.task.VoidTask;
import com.sevenchip.charger.utils.NetworkUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author : Alvin
 * create at : 2020/7/31 17:17
 * description :
 */
public class LanScanDeviceUtils {
    private static final String TAG = LanScanDeviceUtils.class.getSimpleName();

    private static class LanScanDeviceUtilsHolder {
        private static final LanScanDeviceUtils INSTANCE = new LanScanDeviceUtils();
    }

    public static LanScanDeviceUtils getInstance() {
        return LanScanDeviceUtilsHolder.INSTANCE;
    }

    private String mDevAddress;// 本机IP地址-完整
    private String mHostAddressPrefix;// 局域网IP地址头,如：192.168.1.
    private Runtime mRun = Runtime.getRuntime();// 获取当前运行环境，来执行ping，相当于windows的cmd
    private Process mProcess = null;// 进程
    private String mPing = "ping -c 1 -w 3 ";// 其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
    private List<String> mIpList = new ArrayList<>();// ping成功的IP地址
    private Handler mHandler;

    private boolean isScanning = false;

    private LanScanDeviceUtils() {
        mHandler = new Handler(Looper.myLooper());
    }

    public enum ScanStatus {
        SCAN_ERROR,
        SCAN_LOCAL_ADDRESS,
        SCAN_DEVICE_SUCCESS,
        SCAN_DEVICE_FAILED,
        SCAN_COMPLETE,
    }

    private void postUI(ScanStatus status, final String deviceAddress, int scanCode) {
        if (mListener == null) return;
        mHandler.post(() -> {
            switch (status) {
                case SCAN_ERROR:
                    mListener.onScanError();
                    break;
                case SCAN_LOCAL_ADDRESS:
                    mListener.onScanHostAddress(deviceAddress);
                    break;
                case SCAN_DEVICE_SUCCESS:
                    mListener.onScanSuccess(deviceAddress, scanCode);
                    break;
                case SCAN_DEVICE_FAILED:
                    mListener.onScanAddressFail(deviceAddress, scanCode);
                    break;
                case SCAN_COMPLETE:
                    mListener.onScanFinished(mIpList);
                    break;
            }
        });
    }

    public void scan() {
        ThreadExecutor.getInstance().execute(this::scanDevice);
    }

    public void stopScan() {
        isScanning = false;
        ThreadExecutor.getInstance().cancelAll();
        if (mListener != null) {
            mListener.onScanStop();
        }
    }
    private int count = 0;
    private void scanDevice() {
        mIpList.clear();
        mDevAddress = getLocalAddress();
        mHostAddressPrefix = getHostAddressPrefix(mDevAddress);// 获取本地ip前缀
        if (TextUtils.isEmpty(mHostAddressPrefix)) {
            postUI(ScanStatus.SCAN_ERROR, null, -1);
            Log.e(TAG, "扫描失败，请检查wifi网络");
            return;
        }
        postUI(ScanStatus.SCAN_LOCAL_ADDRESS, mDevAddress, -1);
        isScanning = true;
        for (int i = 0; i <= 255; i++) {
            final int lastAddress = i;// 存放ip最后一位地址 1-255
            if (!isScanning) return;
            new VoidTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String ping = LanScanDeviceUtils.this.mPing + mHostAddressPrefix
                            + lastAddress;
                    String currentIp = mHostAddressPrefix + lastAddress;
                    if (mDevAddress.equals(currentIp)) {
                        // 如果与本机IP地址相同,跳过
                        return;
                    }
                    try {
                        mProcess = mRun.exec(ping);
                        int result = mProcess.waitFor();
                        Log.d(TAG, "正在扫描的IP地址为：" + currentIp + "返回值为：" + result);
                        if (result == 0) {
                            Log.d(TAG, "扫描成功,Ip地址为：" + currentIp);
                            postUI(ScanStatus.SCAN_DEVICE_SUCCESS, currentIp, result);
                            mIpList.add(currentIp);
                        } else {
                            // 扫描失败
                            postUI(ScanStatus.SCAN_DEVICE_FAILED, currentIp, result);
                            Log.d(TAG, "扫描失败");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "扫描异常" + e.toString());
                    } finally {
                        if (mProcess != null)
                            mProcess.destroy();
                    }
                }

                @Override
                public void onExecuteFinished() {
                    count++;
                    if (count == 255) {
                        postUI(ScanStatus.SCAN_COMPLETE, null, -1);
                        isScanning = false;
                    }
                }
            }.execute();
        }
    }

    public boolean isScanning() {
        return isScanning;
    }

    private String getLocalAddress() {
        String hostAddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface networks = en.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> address = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (!ip.isLoopbackAddress()
                            && NetworkUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        hostAddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("", "获取本地ip地址失败");
            e.printStackTrace();
        }
        Log.i(TAG, "本机IP:" + hostAddress);
        return hostAddress;
    }

    /**
     * 获取hostAddress前缀
     *
     * @param hostAddress 192.168.31.1
     * @return 192.168.31
     */
    private String getHostAddressPrefix(String hostAddress) {
        if (!hostAddress.equals("")) {
            return hostAddress.substring(0, hostAddress.lastIndexOf(".") + 1);
        }
        return null;
    }

    public interface OnScanListener {
        void onScanHostAddress(String address);

        void onScanError();

        void onScanSuccess(String address, int code);

        void onScanAddressFail(String address, int code);

        void onScanFinished(List<String> devList);

        void onScanStop();
    }

    private OnScanListener mListener;

    public void setListener(OnScanListener listener) {
        this.mListener = listener;

    }

    public void kill(){
        stopScan();
        this.mListener = null;
        ThreadExecutor.getInstance().shutdown();
    }
}