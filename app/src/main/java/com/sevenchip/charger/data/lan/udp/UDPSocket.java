package com.sevenchip.charger.data.lan.udp;

import android.util.Log;

import com.sevenchip.charger.utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author : alvin
 * CreateTime : 2020/8/4 23:04
 * Email : 923080261@qq.com
 * Description :
 */
public class UDPSocket {

    private static final String TAG = "UDPSocket";

    private static final int BROADCAST_PORT = 5000;

    // 单个CPU线程池大小
    private static final int POOL_SIZE = 2;

    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];


    private String mBroadcastIp;
    private int mClientPort = BROADCAST_PORT;

    private boolean isThreadRunning = false;

    private DatagramSocket mClient;
    private DatagramPacket receivePacket;

    private long lastReceiveTime = 0;
    private static final long TIME_OUT = 120 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 10 * 1000;

    private ExecutorService mThreadPool;
    private Thread clientThread;
    private HeartbeatTimer timer;

    public UDPSocket(String ip, int port) {
        this();
        mBroadcastIp = ip;
        mClientPort = port;
    }

    public UDPSocket(String ip) {
        this(ip, BROADCAST_PORT);
    }

    public UDPSocket() {
        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();
    }

    public void setIpPort(String ip, int port) {
        mBroadcastIp = ip;
        mClientPort = port;
    }

    /**
     * 单利模式获取唯一的client
     *
     * @return udp client
     */
    public DatagramSocket getClient() {
        if (null == mClient) {
            synchronized (UDPSocket.class) {
                if (null == mClient) {
                    try {
                        mClient = new DatagramSocket(BROADCAST_PORT);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mClient;
    }

    /**
     * 开启UPD数据包端口监听
     */
    public void startUDPSocket() {
        if (mClient != null) return;
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            mClient = new DatagramSocket(mClientPort);
            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }
            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启发送数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(() -> {
            Log.d(TAG, "clientThread is running...");
            receiveMessage();
        });
        isThreadRunning = true;
        clientThread.start();
        //startHeartbeatTimer();
    }

    public boolean isStartReceive() {
        return isThreadRunning;
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (mClient != null) {
                    mClient.receive(receivePacket);
                }
                lastReceiveTime = System.currentTimeMillis();
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                if (mListener != null) {
                    mListener.onDataReceiveError(e.getMessage());
                }
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            if (mListener != null) {
                mListener.onDataReceive(receivePacket.getData());
            }

            String strReceive = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Log.d(TAG, strReceive + " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (mClient != null) {
            mClient.close();
            mClient = null;
        }
        if (timer != null) {
            timer.exit();
        }
    }

    /**
     * 启动心跳，timer 间隔十秒
     */
    private void startHeartbeatTimer() {
        timer = new HeartbeatTimer();
        timer.setOnScheduleListener(() -> {
            Log.d(TAG, "timer is onSchedule...");
            long duration = System.currentTimeMillis() - lastReceiveTime;
            Log.d(TAG, "duration:" + duration);
            if (duration > TIME_OUT) {//若超过两分钟都没收到我的心跳包，则认为对方不在线。
                Log.d(TAG, "超时，对方已经下线");
                // 刷新时间，重新进入下一个心跳周期
                lastReceiveTime = System.currentTimeMillis();
            } else if (duration > HEARTBEAT_MESSAGE_DURATION) {//若超过十秒他没收到我的心跳包，则重新发一个。
                String string = "hello,this is a heartbeat message";
                sendMessage(string);
            }
        });
        timer.startTimer(0, 1000 * 3);
    }

    /**
     * 发送心跳包
     *
     * @param message
     */
    public void sendMessage(final String message) {
        mThreadPool.execute(() -> {
            try {
                InetAddress targetAddress = InetAddress.getByName(mBroadcastIp);

                DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, mClientPort);
                Log.d(TAG, "-------------------send msg = " + message + " HEX = " + ByteUtils.byteArray2HexStringWithSpaces(message.getBytes(), message.getBytes().length));

                Log.d(TAG, "开始发送++++++++++++++++++++++++++++++");
                getClient().send(packet);
                Log.d(TAG, "发送结束++++++++++++++++++++++++++++++");
                // 数据发送事件
                Log.d(TAG, "数据发送成功");
                if (mListener != null) {
                    mListener.onDataSendComplete(message.getBytes());
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onDataSendError(message.getBytes(), e.getMessage());
                }
            }
        });
    }

    public interface OnLanMesssageListener {
        void onDataReceive(byte[] data);

        void onDataSendComplete(byte[] data);

        void onDataSendError(byte[] data, String errorMsg);

        void onDataReceiveError(String errorMsg);
    }

    private OnLanMesssageListener mListener;

    public void setOnLanMessageListener(OnLanMesssageListener listener) {
        this.mListener = listener;
    }
}
