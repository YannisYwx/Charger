package com.sevenchip.charger.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerTaskUtils {
	ScheduledExecutorService mPool = null;
	public static final int PERIOD = 50;

	private static final class TimerTaskUtilsHolder {
		private static final TimerTaskUtils INSTANCE = new TimerTaskUtils();
	}

	private TimerTaskUtils() {
	}

	public static TimerTaskUtils getInstance() {
		return TimerTaskUtilsHolder.INSTANCE;
	}

	public void executeTask(Runnable task) {
		stopCurrentTask();
		mPool = Executors.newScheduledThreadPool(1);
		mPool.scheduleAtFixedRate(task, 0, PERIOD, TimeUnit.MILLISECONDS);
	}

	public void executeTask(Runnable task, int initialDelay) {
		stopCurrentTask();
		mPool = Executors.newScheduledThreadPool(1);
		mPool.scheduleAtFixedRate(task, initialDelay, PERIOD, TimeUnit.SECONDS);
	}

	public void executeTask(Runnable task, int initialDelay, int period) {
		stopCurrentTask();
		mPool = Executors.newScheduledThreadPool(1);
		mPool.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}

	public void stopCurrentTask() {
		if (mPool != null) {
			mPool.shutdown();
			mPool = null;
		}
	}
}
