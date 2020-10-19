package com.sevenchip.charger.utils;

import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : alvin
 * CreateTime : 2017/9/16  15:49
 * description : 线程池管理类
 */
public class ThreadPoolManager {

    /**
     * 主线程调度
     */
    private final Handler delivery;
    /**
     * 存活时间
     */
    private static final int KEEP_ALIVE_TIME = 5;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private final LongSparseArray<Future> futureArray;
    private final AtomicInteger count;

    public static ThreadPoolManager getInstance() {
        return ThreadPoolManagerHolder.INSTANCE;
    }

    private static class ThreadPoolManagerHolder {
        private static ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }

    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 120;
    private ThreadPoolExecutor executor;

    private ThreadPoolManager() {
        /*
         * 核心线程池的数量，同时能够执行的线程数量
         * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
         */
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        /*
         * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
         * 虽然maximumPoolSize用不到，但是需要赋值，否则报错
         */
        int maximumPoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(
                /*
                 * 当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                 */
                corePoolSize,
                /*
                 * 5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,但是它的数量是包含了corePoolSize的
                 */
                maximumPoolSize,
                /*
                 * 表示的是maximumPoolSize当中等待任务的存活时间
                 */
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                /*
                 * 缓冲队列，用于存放等待任务，Linked的先进先出
                 */
                new LinkedBlockingQueue<Runnable>(),
                /*
                 * 创建线程的工厂
                 */
                new NameThreadFactory(),
                /*
                 * 用来对超出maximumPoolSize的任务的处理策略
                 */
                new ThreadPoolExecutor.AbortPolicy()
        );
        delivery = new Handler();
        futureArray = new LongSparseArray<>();
        count = new AtomicInteger(0);
    }

    /**
     * 执行任务
     * @param run 任务
     */
    public void execute(Runnable run) {
        if (run == null) {
            return;
        }
        executor.execute(run);
    }

    /**
     * 从线程池中移除任务
     * @param run 任务
     */
    public void remove(Runnable run) {
        if (run == null) {
            return;
        }
        executor.remove(run);
    }

    /**
     * 在子线程中执行任务
     * @param run 任务
     * @return key
     */
    public long runOnBackgroundThread(Runnable run) {
        Future future = executor.submit(run);
        long key = count.addAndGet(1);
        futureArray.put(key, future);
        return key;
    }

    /**
     * 在子线程中执行任务
     * @param run 任务
     * @param delay 延迟时间 ms
     * @return key
     */
    public long runOnBackgroundThread(final Runnable run, final long delay) {
        Future future = executor.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(delay);
                        run.run();
                    }
                }
        );
        long key = count.addAndGet(1);
        futureArray.put(key, future);
        return key;
    }

    /**
     * 根据key取消任务
     * @param key
     * @return
     */
    public boolean cancel(long key) {
        Future future = futureArray.get(key);
        if (future == null) {
            return false;
        }

        boolean isSuccess = future.cancel(true);
        if (isSuccess) {
            futureArray.remove(key);
        }
        return isSuccess;
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * 主线程调用
     * @param run 任务
     */
    public void runOnUiThread(Runnable run) {
        delivery.post(run);
    }

    /**
     * 主线程调用
     * @param run 任务
     * @param delay 延迟时间 单位：ms
     */
    public void runOnUiThread(Runnable run, long delay) {
        delivery.postDelayed(run, delay);
    }

    /**
     * 自定义名字的线程工厂
     */
    private static class NameThreadFactory implements ThreadFactory {
        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r);
            String threadName = "ThreadPoolManager -->" + count.addAndGet(1);
            thread.setName(threadName);
            return thread;
        }
    }
}
