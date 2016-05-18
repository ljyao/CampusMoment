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
    //手机cpu数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //线程池的基本大小，默认cpu书加1
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    //线程池中允许的最大线程数，默认cpu数*2+1
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    //空闲线程处存活时间，单位为秒
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
        if (handler == null) {
            synchronized (handlerLock) {
                if (handler == null) {
                    //获取主线程消息泵looper
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
    }

    //初始化有优先级线程池
    private static void initPriorityServicePool() {
        if (priorityService == null) {
            synchronized (priorityLock) {
                if (priorityService != null)
                    return;
                //实现一个有优先级的队列，通过Comparator实现排序
                priorityDeque = new PriorityBlockingQueue<>(1000, new Comparator<Runnable>() {
                    /*
                     *比较r和l的优先级比较
                     *r比l优先级高，则返回1
                     *r比l优先级低，则返回-1
                     * r和l优先级相同，则返回0
                     */
                    @Override
                    public int compare(Runnable lhs, Runnable rhs) {
                        //类型强转前判断对象是否是PriorityRunnable类的一个实例
                        if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                            //类型强转
                            PriorityRunnable l = (PriorityRunnable) lhs;
                            PriorityRunnable r = (PriorityRunnable) rhs;
                            return r.compareTo(l);
                        }
                        return 0;

                    }
                });
                //实例化线程池
                priorityService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                        TimeUnit.SECONDS, priorityDeque);
            }
        }
    }

    private static void initServicePool() {
        synchronized (lock) {
            //使用链表队列
            taskQueue = new LinkedBlockingDeque<>();
            //实例化线程池
            taskService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, taskQueue);
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
        Thread thread = new Thread(runnable);
        thread.start();
        return true;
    }

}
