package com.sevenchip.charger.data.lan.executor.task;

import android.support.annotation.WorkerThread;

/**
 * @author : Alvin
 * create at : 2020/7/31 17:23
 * description :
 */
public interface Task {

    /**
     * 任务执行的方法
     * @return 返回的key
     */
    @WorkerThread
    long execute();

    /**
     * 取消执行
     */
    boolean cancel();
}
