package com.uy.util;

/**
 * Created by nice on 15/11/26.
 */
public abstract class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    //任务计数
    public long priority;

    @Override
    public int compareTo(PriorityRunnable another) {
        return priority > another.priority ? 1 : -1;
    }

}
