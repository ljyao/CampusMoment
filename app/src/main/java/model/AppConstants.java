package model;

import android.os.Environment;

/**
 * Created by sky on 2015/7/6.
 */
public class AppConstants {
    public static final String APP_DIR = Environment.getExternalStorageDirectory() + "/StickerCamera";
    public static final String APP_TEMP = APP_DIR + "/temp";
    public static final String APP_IMAGE = APP_DIR + "/image";
    public static final int REQUEST_EDIT_USERHEAD = 1;
    public static final int REQUEST_PHOTO_FEED = 2;
}
