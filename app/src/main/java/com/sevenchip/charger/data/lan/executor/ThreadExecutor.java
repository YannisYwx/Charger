package com.sevenchip.charger.data.lan.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import com.sevenchip.charger.data.lan.executor.task.ReturnTask;
import com.sevenchip.charger.data.lan.executor.task.VoidTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Alvin
 * create at : 2020/7/31 17:36
 * description :
 */
public class ThreadExecutor implements Executor {
    private static final int CORE_POOL_SIZE = 25;
    private static final int MAX_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 120;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();
    private LongSparseArray<Future> mFutureArray = new LongSparseArray<>();
    private AtomicInteger count = new AtomicInteger(0);

    private Handler mHandler;

    private ThreadPoolExecutor mThreadPoolExecutor;

    private ThreadExecutor() {
        mThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                WORK_QUEUE, new NameThreadFactory());
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void post(@NonNull final VoidTask task) {
        // TODO Auto-generated method stub
        mHandler.post(task::onExecuteFinished);
    }


    private <T> void post(@NonNull final ReturnTask<T> task, final T obj) {
        mHandler.post(() -> {
            // TODO Auto-generated method stub
            task.onExecuteFinished(obj);
        });
    }

    @Override
    public void execute(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public long execute(final VoidTask voidTask) {
        Future future = mThreadPoolExecutor.submit(() -> {
            // TODO Auto-generated method stub
            voidTask.run();
            voidTask.onFinished();
            // notify execute finished
            post(voidTask);
        });
        long key = count.addAndGet(1);
        mFutureArray.put(key, future);
        return key;
    }

    @Override
    public <T> long execute(ReturnTask<T> returnTask) {
        Future future = mThreadPoolExecutor.submit(() -> {
            // TODO Auto-generated method stub
            T result = returnTask.run();
            returnTask.setResponse(result);
            returnTask.onFinished();
            // notify execute finished
            post(returnTask, result);
        });
        long key = count.addAndGet(1);
        mFutureArray.put(key, future);
        return key;
    }

    @Override
    public boolean cancel(long key) {
        Future future = mFutureArray.get(key);
        if (future == null) {
            return false;
        }

        boolean isSuccess = future.cancel(true);
        if (isSuccess) {
            mFutureArray.remove(key);
        }
        return isSuccess;
    }

    public boolean cancelAll() {
        int size = mFutureArray.size();
        for (int i = 0; i < size; i++) {
            long key = mFutureArray.keyAt(i);
            Future future = mFutureArray.get(key);
            if (future != null) {
                boolean isSuccess = future.cancel(true);
                if (!isSuccess) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 关闭 ExecutorService，这将导致其拒绝新任务。提供两个方法来关闭 ExecutorService。
     * shutdown()
     * 方法在终止前允许执行以前提交的任务，
     * shutdownNow()
     * 方法阻止等待任务启动并试图停止当前正在执行的任务。在终止时，执行程序没有任务在执行，
     * 也没有任务在等待执行，并且无法提交新任务。应该关闭未使用的 ExecutorService 以允许回收其资源
     */
    public void shutdown() {
        mThreadPoolExecutor.shutdownNow();
    }

    /**
     * 自定义名字的线程工厂
     */
    private static class NameThreadFactory implements ThreadFactory {
        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r);
            String threadName = "Alvin-" + count.addAndGet(1);
            thread.setName(threadName);
            return thread;
        }
    }

    public static ThreadExecutor getInstance() {
        return ThreadExecutorHolder.INSTANCE;
    }

    private static class ThreadExecutorHolder {
        private static final ThreadExecutor INSTANCE = new ThreadExecutor();
    }
}
