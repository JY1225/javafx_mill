package cn.greatoo.easymill.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ThreadManager {

	private static final int MAX_THREAD_AMOUNT = 20;
	
	private static Logger logger = LogManager.getLogger(ThreadManager.class.getName());
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_AMOUNT);
	
	private static boolean isShuttingDown;
	
	private ThreadManager() {
		ThreadManager.isShuttingDown = false;
	}
	
	public static Future<?> submit(final Runnable runnable) {
		logger.debug("New runnable submitted: [" + runnable + "].");

		return executorService.submit(runnable);
	}
	
	public static void shutDown() {
		ThreadManager.isShuttingDown = true;

		executorService.shutdownNow();
	}
	
	public static boolean isShuttingDown() {
		return ThreadManager.isShuttingDown;
	}

}
