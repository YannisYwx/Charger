package com.sevenchip.charger.data.lan.executor.task;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.sevenchip.charger.data.lan.executor.ThreadExecutor;

/**
 * @author : Alvin
 * create at : 2020/7/31 17:27
 * description :
 */
public abstract class ReturnTask<T> implements Task {

    protected volatile boolean mIsCanceled;
    protected volatile boolean mIsRunning;
    protected long key;

    protected T response;

    /**
     * 所有的耗时任务在这里运行
     * @return 返回的数据
     */
    @WorkerThread
    public abstract T run();

    /**
     * 执行任务结束
     *
     * @param response 返回
     */
    @UiThread
    public abstract void onExecuteFinished(T response);

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

    @Override
    public boolean cancel() {
        boolean isSuccess = ThreadExecutor.getInstance().cancel(key);
        mIsCanceled = true;
        mIsRunning = false;
        return isSuccess;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

}
