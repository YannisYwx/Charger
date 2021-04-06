package com.sevenchip.charger.data.lan.udp;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.sevenchip.charger.data.TestDataMode;
import com.sevenchip.charger.utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author : Alvin
 * CreateTime : 2020/8/9 18:55
 * Email : 923080261@qq.com
 * Description : UDP通讯管理器，可以设置对方的ip地址端口号以及本机监听端口
 */
public class UDPManager {
    private static final String TAG = UDPManager.class.getSimpleName();

    private static final String BROADCAST_IP = "255.255.255.255";
    private static final int BROADCAST_PORT = 5_000;
    private static final int LOCAL_PORT = 4_000;
    // 单个CPU线程池大小
    private static final int POOL_SIZE = 2;
    private ExecutorService mThreadPool;
    private Thread mReceiveThread;
    private ReceiveTask mReceiveTask;

    private DatagramSocket mClient;
    private DatagramPacket mReceivePacket;

    private static final int BUFFER_LENGTH = 128;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private String mBroadcastIp;
    private int mBroadcastPort = BROADCAST_PORT;
    private int mLocalPort = LOCAL_PORT;

    private boolean isReceiveRunning = false;

    private Handler mUIHandler;

    private long mLastReceiveTime = -1L;
    private static final long TIME_OUT = 10 * 1000;

    private HeartbeatTimer timer;

    public enum Status {
        DATA_RECEIVE,
        DATA_SEND_COMPLETE,
        DATA_SEND_ERROR,
        DATA_RECEIVE_ERROR,
        DEVICE_OFFLINE,
    }

    private void postUI(final Status status, final byte[] data, final String errorMsg) {
        if (mListenerList == null || mListenerList.isEmpty()) {
            return;
        }
        mUIHandler.post(() -> {
            switch (status) {
                case DATA_RECEIVE:
                    for (OnLanMessageListener listener : mListenerList) {
                        listener.onDataReceive(data);
                    }
                    break;
                case DATA_SEND_COMPLETE:
                    for (OnLanMessageListener listener : mListenerList) {
                        listener.onDataSendComplete(data);
                    }
                    break;
                case DATA_SEND_ERROR:
                    for (OnLanMessageListener listener : mListenerList) {
                        listener.onDataSendError(data, errorMsg);
                    }
                    break;
                case DATA_RECEIVE_ERROR:
                    for (OnLanMessageListener listener : mListenerList) {
                        listener.onDataReceiveError(errorMsg);
                    }
                    break;
                case DEVICE_OFFLINE:
                    for (OnLanMessageListener listener : mListenerList) {
                        listener.onDeviceOffline();
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private static class UDPManagerHolder {
        private static UDPManager INSTANCE = new UDPManager();
    }

    public static UDPManager getInstance() {
        return UDPManagerHolder.INSTANCE;
    }

    private UDPManager() {
        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        mLastReceiveTime = System.currentTimeMillis();

        mUIHandler = new Handler(Looper.myLooper());
    }

    public void setIpPort(String ip, int broadcastPort, int localPort) {
        mBroadcastIp = ip;
        mBroadcastPort = broadcastPort;
        mLocalPort = localPort;
    }

    public String getBroadcastIp() {
        return mBroadcastIp;
    }

    public void setBroadcastIp(String broadcastIp) {
        this.mBroadcastIp = broadcastIp;
    }

    public int getBroadcastPort() {
        return mBroadcastPort;
    }

    public void setBroadcastPort(int broadcastPort) {
        this.mBroadcastPort = broadcastPort;
    }

    public int getLocalPort() {
        return mLocalPort;
    }

    public void setLocalPort(int localPort) {
        this.mLocalPort = localPort;
    }

    /**
     * 单利模式获取唯一的client
     *
     * @return udp client
     */
    private DatagramSocket getClient() {
        if (null == mClient) {
            synchronized (UDPManager.class) {
                if (null == mClient) {
                    try {
                        mClient = new DatagramSocket(LOCAL_PORT);
//                        mClient = new DatagramSocket(LOCAL_PORT, InetAddress.getByName("0.0.0.0"));
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mClient;
    }

    /**
     * 开始监听广播
     */
    public void startListenerBroadcast() {
        if (isReceiveRunning) {
            stopListenerBroadcast();
        }
        if (mReceivePacket == null) {
            mReceivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
        }
        isReceiveRunning = true;
        mReceiveTask = new ReceiveTask();
        mReceiveThread = new Thread(mReceiveTask);
        mReceiveThread.start();
        startCheckTimer();
    }

    private static final long HEARTBEAT_MESSAGE_DURATION = 10 * 1000;

    /**
     * 启动心跳，timer 间隔十秒
     */
    private void startCheckTimer() {
        stopCheckTimer();
        timer = new HeartbeatTimer();
        timer.setOnScheduleListener(() -> {
            long duration = System.currentTimeMillis() - mLastReceiveTime;
            Log.d(TAG, "duration:" + duration);
            if (duration >= TIME_OUT) {//若超过两分钟都没收到我的心跳包，则认为对方不在线。
                Log.d(TAG, "超时，设备断开连接");
                // 刷新时间，重新进入下一个心跳周期
                postUI(Status.DEVICE_OFFLINE, null, null);
            }
        });
        timer.startTimer(0, 1000 * 2);
    }

    private void stopCheckTimer() {
        if (timer != null) {
            timer.exit();
            timer = null;
        }
    }

    public String listenerHostInfo() {
        return "[ address = " + mBroadcastIp + " , devicePort = " + mBroadcastPort + " , localPort = " + mLocalPort + " ]";
    }

    /**
     * 开始停止监听广播
     */
    public void stopListenerBroadcast() {
        isReceiveRunning = false;
        mReceivePacket = null;
        if (mReceiveThread != null) {
            mReceiveThread = null;
        }
        if (mReceiveTask != null) {
            mReceiveTask = null;
        }
        if (mClient != null) {
            mClient.close();
            mClient = null;
        }
        stopCheckTimer();
    }

    public void stop() {
        stopListenerBroadcast();
        mThreadPool.shutdownNow();
    }

    public boolean isReceiveRunning() {
        return isReceiveRunning;
    }

    /**
     * 发送广播数据
     * host address 255.255.255.255
     * port 50000
     *
     * @param data 下行数据
     */
    public void sendMessage(byte[] data) {
        sendMessage(data, BROADCAST_IP, BROADCAST_PORT);
    }

    public void sendMessage(String message) {
        sendMessage(message.getBytes());
    }

    public void sendMessage(byte[] data, String ip, int port) {
        mThreadPool.execute(() -> {
            try {
                InetAddress inetAddress = InetAddress.getByName(ip);
                DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
                Log.d(TAG, "开始发送++++++++++++++++++++++++++++++");
                getClient().send(packet);
                Log.d(TAG, "发送结束++++++++++++++++++++++++++++++");
                Log.d(TAG, "数据发送成功");
                postUI(Status.DATA_SEND_COMPLETE, data, null);
            } catch (Exception e) {
                e.printStackTrace();
                postUI(Status.DATA_SEND_ERROR, data, e.getMessage());
            }
        });
    }

    private class ReceiveTask implements Runnable {

        @Override
        public void run() {
            Log.d(TAG, "ReceiveTask is running...");
            while (isReceiveRunning) {
                try {
                    Log.d(TAG, "Start receive local port = " + mBroadcastPort);
                    getClient().receive(mReceivePacket);
                    mLastReceiveTime = System.currentTimeMillis();
                    Log.d(TAG, "receive packet success...");
                } catch (IOException e) {
                    Log.e(TAG, "UDP数据包接收失败！线程停止");
                    stopListenerBroadcast();
                    postUI(Status.DATA_RECEIVE_ERROR, null, e.getMessage());
                    e.printStackTrace();
                    return;
                }

                if (mReceivePacket == null || mReceivePacket.getLength() <= 0) {
                    Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                    continue;
                }
                byte[] batteryData = new byte[85];
                System.arraycopy(mReceivePacket.getData(), 0, batteryData, 0, 85);


                postUI(Status.DATA_RECEIVE, batteryData, null);

                String strReceive = new String(batteryData, 0, batteryData.length);
                Log.d(TAG, strReceive + " from " + mReceivePacket.getAddress().getHostAddress() + ":" + mReceivePacket.getPort());

                // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
                if (mReceivePacket != null) {
                    mReceivePacket.setLength(BUFFER_LENGTH);
                }
            }
        }
    }

    public interface OnLanMessageListener {
        void onDataReceive(byte[] data);

        void onDataSendComplete(byte[] data);

        void onDataSendError(byte[] data, String errorMsg);

        void onDataReceiveError(String errorMsg);

        void onDeviceOffline();
    }

    private List<OnLanMessageListener> mListenerList;

    public void addLanMessageListener(OnLanMessageListener listener) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    public void removeLanMessageListener(OnLanMessageListener listener) {
        if (mListenerList != null && !mListenerList.isEmpty()) {
            mListenerList.remove(listener);
        }
    }

    public void removeAllListener() {
        if (mListenerList != null && !mListenerList.isEmpty()) {
            mListenerList.clear();
        }
    }

    private boolean isTestDataRunning = false;

    /**
     * 假数据 任务
     */
    private class TestDataTask implements Runnable {

        @Override
        public void run() {
            int count = 0;
            while (isTestDataRunning) {
                byte[] data;
                count++;
                if (count % 4 == 0) {
                    data = TestDataMode.getCH1();
                } else if (count % 4 == 1) {
                    data = TestDataMode.getCH2();
                } else if (count % 4 == 2) {
                    data = TestDataMode.getCH1_2();
                } else {
                    data = TestDataMode.getCH2_2();
                }
                mLastReceiveTime = System.currentTimeMillis();
                SystemClock.sleep(500);
                postUI(Status.DATA_RECEIVE, data, null);
//                if(count == 48) {
//                    stopTestTask();
//                }
            }
        }
    }

    private Thread mTestThread;
    private TestDataTask mTestTask;

    public void startTestTask() {
        if (isTestDataRunning) return;
        isTestDataRunning = true;
        if (mTestThread == null) {
            mTestTask = new TestDataTask();
            mTestThread = new Thread(mTestTask);
        }
        mTestThread.start();
        startCheckTimer();
    }

    public void stopTestTask() {
        isTestDataRunning = false;
        if (mTestThread != null) {
            mTestThread.interrupt();
            mTestTask = null;
        }
        mTestThread = null;
//        stopCheckTimer();
    }
}
