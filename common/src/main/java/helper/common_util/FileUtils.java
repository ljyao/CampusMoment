package helper.common_util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;


public class FileUtils {

    private static final String IMAGE_DIR = "/Picture";
    private static final String APP_DIR = "/CampusMonment";

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 判断SD卡是否可用
     */
    public static boolean isStorageOK() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡跟路径。SD卡不可用时，返回null
     */
    public static String getStorageRoot() {
        if (isStorageOK()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡跟路径。SD卡不可用时，返回cache
     */
    public static String getStorageRoot(Context context) {
        String path = getStorageRoot();
        if (path == null) {
            path = getCacheDir(context);
        }
        return path;
    }

    /**
     * 判断该文件是否是一个图片。
     */
    public static boolean isImage(String fileName, String filePath) {
        //            BitmapFactory.Options opt = new BitmapFactory.Options();
//            opt.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(filePath, opt);
//
//            // 获取到这个图片的原始宽度和高度
//            float picWidth = opt.outWidth;
//            float picHeight = opt.outHeight;
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png");
    }

    public static String getCacheDir(Context context) {
        File mSystemCacheDir = context.getCacheDir();
        return mSystemCacheDir.getPath();
    }

    public static String getImageCacheDir(Context context) {
        try {
            String dirPath = getCacheDir(context) + IMAGE_DIR;
            File cacheDir = new File(dirPath);
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            return cacheDir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImageStorageDir(Context context) {
        try {
            String dirPath = getStorageRoot(context) + APP_DIR;
            File appDir = new File(dirPath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String imgPath = dirPath + IMAGE_DIR;
            File imgDir = new File(imgPath);
            if (!imgDir.exists()) {
                imgDir.mkdir();
            }
            return imgDir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean createBitmapFile(File imgFile, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            if (imgFile.exists()) {
                imgFile.delete();
            } else {
                imgFile.createNewFile();
            }
            //创建文件
            imgFile.createNewFile();
            out = new FileOutputStream(imgFile);
            //保存压缩后的图片
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String createBitmapFile(String name, Bitmap bitmap, Context context) {
        String cacheDirPath = context.getCacheDir().getPath() + "/imageCache";

        File cacheDir = new File(cacheDirPath);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        File imgFile = new File(cacheDir, name);
        createBitmapFile(imgFile, bitmap);
        return imgFile.getAbsolutePath();
    }

}
