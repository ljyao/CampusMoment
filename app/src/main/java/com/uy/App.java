package com.uy;

import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;

/**
 * Created by nice on 15/11/20.
 */
public class App extends MultiDexApplication {
    private static CommunitySDK communitySDK;
    private static App appContext;
    private DisplayMetrics displayMetrics;

    public static CommunitySDK getCommunitySDK() {
        return communitySDK;
    }

    public static App getApp() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            appContext = this;
            Fresco.initialize(this);
            //创建CommUser前必须先初始化CommunitySDK
            communitySDK = CommunityFactory.getCommSDK(this);
        } catch (Exception e) {
        }
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f) {
        return (int) (0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    //获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    //获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }
}
