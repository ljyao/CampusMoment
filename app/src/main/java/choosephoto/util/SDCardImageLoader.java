package choosephoto.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import com.uy.util.BitmapUtils;
import com.uy.util.CompressType;
import com.uy.util.PriorityRunnable;
import com.uy.util.Worker;

import java.io.File;

import helper.common_util.FileUtils;
import helper.common_util.ScreenUtils;

/**
 * 从SDCard异步加载图片
 */
public class SDCardImageLoader {

    // 缓存
    private LruCache<String, Bitmap> imageCachePool;
    private long counter = 0;
    private int screenW, screenH;
    private File mSystemCacheDir;
    private Context mContext;
    private String cacheDirPath;

    public SDCardImageLoader(Context context) {
        this.screenW = ScreenUtils.getScreenW(context);
        this.screenH = ScreenUtils.getScreenH(context);
        mContext = context;
        mSystemCacheDir = context.getCacheDir();
        cacheDirPath = mSystemCacheDir.getPath() + "/imageCache";

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 2;

        // 设置图片缓存大小为程序最大可用内存
        if (imageCachePool == null) {
            imageCachePool = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }
    }


    /**
     * @param smallRate
     * @param filePath
     * @param callback
     * @return
     */
    public Bitmap loadDrawable(CompressType type, final int smallRate, final String filePath, final ImageCallback callback) {
        // 如果缓存过就从缓存中取出数据

        if (imageCachePool.get(filePath) != null) {
            Bitmap bitmap = imageCachePool.get(filePath);
            callback.imageLoaded(bitmap);
            return bitmap;
        }
        // 如果缓存没有则读取SD卡
        LoadImageTask runnable = new LoadImageTask(type, smallRate, filePath, callback);
        Worker.postPriorityTask(runnable);
        return null;
    }

    /**
     * 异步读取SD卡图片，并按指定的比例进行压缩（最大不超过屏幕像素数）
     *
     * @param smallRate 压缩比例，不压缩时输入1，此时将按屏幕像素数进行输出
     * @param filePath  图片在SD卡的全路径
     * @param imageView 组件
     */
    public void loadImage(CompressType type, int smallRate, final String filePath, final ImageView imageView) {

        loadDrawable(type, smallRate, filePath, new ImageCallback() {
            @Override
            public void imageLoaded(Bitmap bmp) {
                if (imageView.getTag().equals(filePath)) {
                    if (bmp != null) {
                        imageView.setImageBitmap(bmp);
                    }
                }
            }
        });

    }


    // 回调接口
    public interface ImageCallback {
        void imageLoaded(Bitmap imageDrawable);
    }

    public class LoadImageTask extends PriorityRunnable {

        private CompressType compressType;
        private int smallRate;
        private String filePath;
        private ImageCallback callback;

        public LoadImageTask(CompressType compressType, int smallRate, String filePath, ImageCallback callback) {
            this.callback = callback;
            this.filePath = filePath;
            this.compressType = compressType;
            this.smallRate = smallRate;
            counter++;
            priority = counter;
        }

        @Override
        public void run() {
            try {

                File cacheDir = new File(cacheDirPath);
                if (!cacheDir.exists()) {
                    cacheDir.mkdir();
                }
                File imgFile = new File(filePath);
                File imgCacheFile = new File(cacheDir, imgFile.getName());
                if (imgCacheFile.exists()) {
                    final Bitmap bitmap = BitmapFactory.decodeFile(imgCacheFile.getPath());
                    imageCachePool.put(filePath, bitmap);
                    Worker.postMain(new Runnable() {
                        @Override
                        public void run() {
                            callback.imageLoaded(bitmap);
                        }
                    });

                } else {
                    loadSDFileImage(imgCacheFile);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private boolean loadSDFileImage(File imgFile) {
            final Bitmap bmp = BitmapUtils.compressBitmap(compressType, filePath, smallRate, (Activity) mContext);
            // 存入map
            imageCachePool.put(filePath, bmp);

            Worker.postMain(new Runnable() {
                public void run() {
                    callback.imageLoaded(bmp);
                }
            });

            FileUtils fileUtils = new FileUtils();
            fileUtils.createBitmapFile(imgFile, bmp);
            return false;
        }

    }
}
