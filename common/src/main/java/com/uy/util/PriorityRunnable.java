package com.uy.util;

/**
 * Created by nice on 15/11/26.
 */
public abstract class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {
    public long priority;

    @Override
    public int compareTo(PriorityRunnable another) {
        return priority > another.priority ? 1 : -1;
    }

}
