package com.sevenchip.charger.data.lan.udp;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author : alvin
 * CreateTime : 2020/8/4 23:04
 * Email : 923080261@qq.com
 * Description :
 */
public class HeartbeatTimer {

    private Timer timer;
    private TimerTask task;
    private OnScheduleListener mListener;

    HeartbeatTimer() {
        timer = new Timer();
    }

    public void startTimer(long delay, long period) {
        task = new TimerTask() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onSchedule();
                }
            }
        };
        timer.schedule(task, delay, period);
    }

    public void exit() {
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    public interface OnScheduleListener {
        void onSchedule();
    }

    public void setOnScheduleListener(OnScheduleListener listener) {
        this.mListener = listener;
    }
}
