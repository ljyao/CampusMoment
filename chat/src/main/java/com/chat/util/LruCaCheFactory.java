package com.chat.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by nice on 15/11/30.
 */
public class LruCaCheFactory {
    private static Object lock = new Object();
    private static LruCache<String, Bitmap> emojiCache = null;
    private static int maxMemory = (int) Runtime.getRuntime().maxMemory();

    public static LruCache<String, Bitmap> getEmojiCache() {
        if (emojiCache == null) {
            synchronized (lock) {
                if (emojiCache == null) {
                    int cacheSize = maxMemory / 8;
                    // 设置图片缓存大小为程序最大可用内存的1/8
                    if (emojiCache == null) {
                        emojiCache = new LruCache<String, Bitmap>(cacheSize) {
                            @Override
                            protected int sizeOf(String key, Bitmap value) {
                                return value.getByteCount();
                            }
                        };
                    }
                }
            }
        }
        return emojiCache;
    }
}
