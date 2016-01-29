package com.chat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.uy.util.ScreenUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    public static final String SP_ReceiveSTATUS = "ReceiveSTATUS";
    public static final int ReceiveMESSAGE = 1;
    public static final int NOTReceiveSTATUS = -1;
    public static final String SP_CHAT_CONFIG = "chat_config";
    public static final String SP_OPEN_FLAG = "open_flag";
    public static final int SENDSUCCESS = 1;
    public final static int SENDFAIL = -1;
    public static final int waiting = 0;
    public static final int receivemsg = 2;
    private static LruCache<String, Bitmap> imageCache = null;

    /**
     * 图片质量压缩
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getCompressImage(String path) {
        // 获取宽度
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // ////******

		/* 真正的返回一个Bitmap */
        options.inJustDecodeBounds = false;
        options.inSampleSize = options.outWidth / 200;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 保存图片
     */
    public static boolean saveBitmap2File(Bitmap bmp, String path) {
        OutputStream stream = null;
        CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        try {
            stream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (stream == null) {
            return false;
        } else {
            return bmp.compress(format, quality, stream);
        }
    }

    /**
     * @param context
     * @param content
     * @return 转成表情的字符串
     */
    public static SpannableStringBuilder textToImage(Context context, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(png));
                BitmapDrawable bd = new BitmapDrawable(bitmap);
                Drawable drawable = bd;
                int val = ScreenUtils.dp2px(30, context);
                drawable.setBounds(0, 0, val, val);
                sb.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb;
    }


    /**
     * 获取SD卡图片文件夹
     *
     * @param context
     * @return /sdcard/android/data/packagename/files/image/
     */
    public static String getExternalImageDir(Context context) {
        return context.getExternalFilesDir(null) + "/image/";
    }

}
