package com.sevenchip.charger.data.lan.udp;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.Socket;

/**
 * Author : Alvin
 * CreateTime : 2020/8/10 23:07
 * Email : 923080261@qq.com
 * Description :
 */
public class TCPManager {
    private static final int DEVICE_PORT = 5050;
    private Socket mSocket;
    private static TCPManager instance;
    private Handler mHandler;
    private String ip;
    private int port = DEVICE_PORT;
    private boolean isConnect;

    private TCPManager() {
        mHandler = new Handler(Looper.myLooper());
        isConnect = false;
    }

    public static TCPManager getInstance() {
        if (null == instance) {
            synchronized (TCPManager.class) {
                if (null == instance) {
                    instance = new TCPManager();
                }
            }
        }
        return instance;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
        if (mSocket == null) {
            new Thread(() -> {
                try {
                    mSocket = new Socket(ip, port);
                    isConnect = true;
                    if (mListener != null) {
                        mHandler.post(() -> mListener.onTCPConnect(ip));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    close();
                    if (mListener != null) {
                        mHandler.post(() -> mListener.onTCPConnectError(ip, e.getMessage()));
                    }
                }
            }).start();

        }
    }

    private void close() {
        isConnect = false;
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mSocket = null;
            }
        }
    }

    public void sendMessage(String msg) {
        if (mSocket != null && mSocket.isConnected()) {
            new Thread(() -> {
                try {
                    mSocket.getOutputStream().write(msg.getBytes());
                    mSocket.getOutputStream().flush();
                    if (mListener != null) {
                        mHandler.post(() -> mListener.onMessageSendComplete(ip));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mListener != null) {
                        mHandler.post(() -> mListener.onMessageSendError(ip, e.getMessage()));
                    }
                }
            }).start();
        }
    }

    public void sendMessage(String ip, int port, String msg) {
        this.ip = ip;
        this.port = port;
        new Thread(() -> {
            try {
                if (mSocket == null) {
                    mSocket = new Socket(this.ip, this.port);
                }
                mSocket.getOutputStream().write(msg.getBytes());
                mSocket.getOutputStream().flush();
                if (mListener != null) {
                    mHandler.post(() -> mListener.onMessageSendComplete(ip));
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mHandler.post(() -> mListener.onMessageSendError(ip, e.getMessage()));
                }
            }
        }).start();
    }

    private TCPConnectListener mListener;

    public void setListener(TCPConnectListener listener) {
        this.mListener = listener;
    }

    public interface TCPConnectListener {
        void onTCPConnect(String ip);

        void onTCPConnectError(String ip, String errorMsg);

        void onMessageSendComplete(String ip);

        void onMessageSendError(String ip, String errorMsg);

    }

}
