package com.uy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;


public class MyFileUtils {


    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static String createBitmapFile(String name, Bitmap bitmap, Context context) {
        String cacheDirPath = context.getCacheDir().getPath() + "/imageCache";

        File cacheDir = new File(cacheDirPath);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        File imgFile = new File(cacheDir, name);
        createBitmapFile(imgFile, bitmap);
        return imgFile.getAbsolutePath();
    }

    public static boolean createBitmapFile(File imgFile, Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            if (imgFile.exists()) {
                imgFile.delete();
            }
            imgFile.createNewFile();
            out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断SD卡是否可用
     */
    public static boolean isSDcardOK() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡跟路径。SD卡不可用时，返回null
     */
    public static String getSDcardRoot() {
        if (isSDcardOK()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 判断该文件是否是一个图片。
     */
    public static boolean isImage(String fileName, String filePath) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opt);

            // 获取到这个图片的原始宽度和高度
            float picWidth = opt.outWidth;
            float picHeight = opt.outHeight;
            return !(picHeight <= 0 || picWidth <= 0);
        } else {
            return false;
        }
    }


}
