package com.sevenchip.charger.data.lan.executor;

import com.sevenchip.charger.data.lan.executor.task.ReturnTask;
import com.sevenchip.charger.data.lan.executor.task.VoidTask;

/**
 * @author : Alvin
 * create at : 2020/7/31 17:35
 * description :
 */
public interface Executor {

    void execute(Runnable runnable);

    /**
     * execute the task
     *
     * @param voidTask task
     * @return the key of task
     */
    long execute(final VoidTask voidTask);

    /**
     * execute the task
     *
     * @param returnTask task
     * @return the key of task
     */
    <T> long execute(final ReturnTask<T> returnTask);

    /**
     * cancel the task by key
     *
     * @param key key
     * @return true:success false:fail
     */
    boolean cancel(long key);

}
