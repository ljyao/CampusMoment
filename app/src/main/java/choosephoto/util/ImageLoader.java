package choosephoto.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.uy.util.BitmapUtils;
import com.uy.util.CompressType;
import com.uy.util.PriorityRunnable;
import com.uy.util.Worker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import choosephoto.adapter.PhotoAlbumLVItem;
import helper.common_util.FileUtils;
import helper.common_util.ScreenUtils;

/**
 * 从SDCard异步加载图片
 */
public class ImageLoader {
    private static ImageLoader loader;
    private static ArrayList<PhotoAlbumLVItem> albumLVItems;
    // 缓存
    private LruCache<String, Bitmap> imageCachePool;
    private long counter = 0;
    private int screenW, screenH;
    private File mSystemCacheDir;
    private Context mContext;
    private String cacheDirPath;

    private ImageLoader(Context context) {
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

    public static ImageLoader getInstance(Context context) {
        if (loader == null) {
            loader = new ImageLoader(context);
        }
        return loader;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    public static ArrayList<PhotoAlbumLVItem> getImagePaths(Context context) {
        albumLVItems = new ArrayList<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        ContentResolver mContentResolver = context.getContentResolver();

        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri,
                new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                        + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        if (cursor != null) {
            if (cursor.moveToLast()) {
                // 路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<>();

                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);

                    final File parentFile = new File(imagePath).getParentFile();
                    final String parentPath = parentFile.getAbsolutePath();

                    // 不扫描重复路径
                    if (!cachePath.contains(parentPath)) {
                        albumLVItems.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile), getFirstImagePath(parentFile)));
                        cachePath.add(parentPath);
                    }

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }

            cursor.close();
        }

        return albumLVItems;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static ArrayList<String> getLatestImagePaths(Context context, int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri,
                new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                        + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            // 从最新的图片开始读取.
            // 当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    if (BitmapUtils.isImage(path)) {
                        latestImagePaths.add(path);
                    }

                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return latestImagePaths;
    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    public static ArrayList<String> getAllImagePathsByFolder(String folderPath) {
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return null;
        }

        ArrayList<String> imageFilePaths = new ArrayList<String>();
        for (int i = allFileNames.length - 1; i >= 0; i--) {
            String filePath = folderPath + File.separator + allFileNames[i];
            if (FileUtils.isImage(allFileNames[i], filePath)) {
                imageFilePaths.add(filePath);
            }
        }

        return imageFilePaths;
    }

    /**
     * 获取目录中图片的个数。
     */
    private static int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            if (FileUtils.isImage(file.getName(), file.getAbsolutePath())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private static String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            if (FileUtils.isImage(file.getName(), file.getAbsolutePath())) {
                return file.getAbsolutePath();
            }
        }

        return null;
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

    public void destroy() {
        imageCachePool.evictAll();
        imageCachePool = null;
        loader = null;
        System.gc();
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
