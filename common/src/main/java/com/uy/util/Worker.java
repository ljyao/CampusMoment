package com.uy.util;

import android.os.Handler;
import android.os.Looper;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by nice on 15/11/26.
 */
public class Worker {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static Handler handler = null;
    private static Object lock = new Object();
    private static Object handlerLock = new Object();
    private static Object priorityLock = new Object();
    private static BlockingQueue<Runnable> priorityDeque;
    private static ExecutorService priorityService;
    private static BlockingQueue<Runnable> taskQueue;
    private static ExecutorService taskService;

    private static void initHandler() {
        synchronized (handlerLock) {
            handler = new Handler(Looper.getMainLooper());
        }
    }

    private static void initPriorityServicePool() {
        synchronized (priorityLock) {
            priorityDeque = new PriorityBlockingQueue<>(1000, new Comparator<Runnable>() {
                @Override
                public int compare(Runnable lhs, Runnable rhs) {
                    if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                        PriorityRunnable l = (PriorityRunnable) lhs;
                        PriorityRunnable r = (PriorityRunnable) rhs;
                        return r.compareTo(l);
                    }
                    return 0;

                }
            });
            priorityService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, priorityDeque);
        }
    }

    private static void initServicePool() {
        synchronized (lock) {
            taskQueue = new LinkedBlockingDeque<>();
            taskService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, taskQueue);
        }
    }

    public static boolean postMain(Runnable runnable) {
        if (handler == null) {
            initHandler();
        }
        handler.post(runnable);
        return true;
    }

    public static boolean postMain(Runnable runnable, long delayed) {
        if (handler == null) {
            initHandler();
        }
        handler.postDelayed(runnable, delayed);
        return true;
    }

    public static boolean postPriorityTask(PriorityRunnable priorityRunnable) {
        if (priorityService == null) {
            initPriorityServicePool();
        }
        priorityService.submit(priorityRunnable);
        return true;
    }

    public static boolean postExecuteTask(Runnable runnable) {
        if (taskService == null) {
            initServicePool();
        }
        taskService.execute(runnable);
        return true;
    }

    public static boolean postThread(Runnable runnable) {
        Thread thread=new Thread(runnable);
        thread.start();
        return true;
    }

}
