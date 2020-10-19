package com.sevenchip.charger.data.lan.executor.task;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.sevenchip.charger.data.lan.executor.ThreadExecutor;

/**
 * @author : Alvin
 * create at : 2020/7/31 17:27
 * description :
 */
public abstract class VoidTask implements Task {
    protected volatile boolean mIsCanceled;
    protected volatile boolean mIsRunning;
    protected long key;

    @WorkerThread
    public abstract void run();

    public boolean isRunning() {
        return mIsRunning;
    }

    public void onFinished() {
        mIsRunning = false;
        mIsCanceled = false;
    }

    @Override
    public long execute() {
        mIsRunning = true;
        key = ThreadExecutor.getInstance().execute(this);
        return key;
    }

    /**
     * 执行任务结束
     */
    @UiThread
    public abstract void onExecuteFinished();

    @Override
    public boolean cancel() {
        boolean isSuccess = ThreadExecutor.getInstance().cancel(key);
        mIsCanceled = true;
        mIsRunning = false;
        return isSuccess;
    }

}
