package com.uy.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import helper.common_util.ScreenUtils;

/**
 * Created by nice on 15/12/1.
 */
public class BitmapUtils {
    public static Bitmap compressBitmap(CompressType compressType, String filePath, int smallRate, Activity mContext) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        float picWidth = opt.outWidth;
        float picHeight = opt.outHeight;

        // 读取图片失败时直接返回
        if (picWidth == 0 || picHeight == 0) {
            return null;
        }

        if (picHeight / picWidth > 2) {
            compressType = CompressType.COMPRESS_SCALE;
            smallRate = 4;
        }

        // 初始压缩比例
        opt.inSampleSize = smallRate;
        if (compressType == CompressType.COMPRESS_SMART) {
            // 根据屏的大小和图片大小计算出缩放比例
            if (picWidth > picHeight) {
                if (picWidth > ScreenUtils.getScreenW(mContext))
                    opt.inSampleSize = (int) (picWidth / (float) ScreenUtils.getScreenW(mContext));
            } else {
                if (picHeight > ScreenUtils.getScreenH(mContext))
                    opt.inSampleSize = (int) (picHeight / (float) ScreenUtils.getScreenH(mContext));
            }
        } else if (compressType == CompressType.COMPRESS_DP) {
            float w = ScreenUtils.dp2px(smallRate, mContext);
            float h = (w / picWidth) * picHeight;
            opt.inSampleSize = calculateInSampleSize(opt, w, h);
        } else if (compressType == CompressType.COMPRESS_NO) {
            opt.inSampleSize = 1;
        } else if (compressType == CompressType.COMPRESS_SCALE) {
            opt.inSampleSize = smallRate;
        } else if (compressType == CompressType.COMPRESS_PX) {
            float w = smallRate;
            float h = (w / picWidth) * picHeight;
            opt.inSampleSize = calculateInSampleSize(opt, w, h);
        }

        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);
        return bmp;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / reqHeight);
            final int widthRatio = Math.round((float) width / reqWidth);

            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * bitmap转成base64
     *
     * @param bitmap bitmap
     * @return base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        // 将Bitmap转换成字符串
        String base64;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64;
    }

    /**
     * base64转成bitmap
     *
     * @param base64 base64
     * @return bitmap
     */
    public static Bitmap base64ToBitmap(String base64) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static boolean isImage(String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);

        // 获取到这个图片的原始宽度和高度
        float picWidth = opt.outWidth;
        float picHeight = opt.outHeight;
        return !(picHeight <= 100 || picWidth <= 100);
    }
}
