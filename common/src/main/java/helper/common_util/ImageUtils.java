package helper.common_util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.uy.util.Worker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    private static Map<String, Bitmap> bitmapCache = new HashMap<>();

    public static void putBitmap(String key, Bitmap bitmap) {
        bitmapCache.put(key, bitmap);
    }

    public static String saveBitmapToStorage(final Context context, final Bitmap bitmap, final OnSaveBitmapListener listener) {
        String name = TimeUtils.getTime() + ".jpg";
        final String path = FileUtils.getImageStorageDir(context) + "/" + name;
        final File imgFile = new File(path);
        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                FileUtils.createBitmapFile(imgFile, bitmap);
                updateImageToMediaLibrary(context, imgFile);
                Worker.postMain(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(path);
                    }
                });
            }
        });
        return path;
    }

    public static void updateImageToMediaLibrary(Context context, File imgFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imgFile);
        mediaScanIntent.setData(contentUri);
        // 发布广播,更新媒体库
        context.sendBroadcast(mediaScanIntent);
    }

    public static Bitmap getBitmap(String key) {
        return bitmapCache.get(key);
    }

    public static Bitmap RemoveBitmap(String key) {
        return bitmapCache.remove(key);
    }

    public static void setImagePath(final ImageView imageView, final String path) {
        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeFile(path);
                Worker.postMain(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    public static int getMiniSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return Math.min(options.outHeight, options.outWidth);
    }

    public static int getImageWidth(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outWidth;
    }

    public static int getImageHeight(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outHeight;
    }

    public static int[] getImageRect(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int[] rect = {options.outWidth, options.outHeight};
        return rect;
    }

    public static boolean isSquare(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options.outHeight == options.outWidth;
    }

    public static Bitmap getBitmapFromView(View view) {
        System.gc();
        Bitmap returnedBitmap = null;
        try {
            returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return the bitmap
        return returnedBitmap;
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public interface OnSaveBitmapListener {
        void onComplete(String path);
    }
}
