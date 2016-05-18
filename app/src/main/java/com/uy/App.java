package com.uy;

import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.community.location.DefaultLocationImpl;

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
            LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl());
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
            //创建CommUser前必须先初始化CommunitySDK
            communitySDK = CommunityFactory.getCommSDK(this);
        } catch (Exception e) {
        }
    }
}
