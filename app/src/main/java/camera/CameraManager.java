package camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.util.Stack;

import camera.ui.CameraActivity;
import editimage.EditImageActivity;
import helper.common_util.ImageUtils;

public class CameraManager {

    private static CameraManager mInstance;
    private Stack<Activity> cameras = new Stack<Activity>();

    public static CameraManager getInst() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new CameraManager();
            }
        }
        return mInstance;
    }

    //打开照相界面
    public void openCamera(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }


    public void processPhotoItem(Activity activity, Bitmap photo) {
        String key = "photoBmpCache";
        ImageUtils.putBitmap(key, photo);
        Intent intent = new Intent(activity, EditImageActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("isFromCache", true);
        activity.startActivity(intent);
        /*Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        Intent i = new Intent(activity, CropPhotoActivity_.class);
        i.setData(uri);
        //TODO稍后添加
        activity.startActivityForResult(i, AppConstants.REQUEST_CROP);*/

    }

    public void close() {
        for (Activity act : cameras) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        cameras.clear();
    }

    public void addActivity(Activity act) {
        cameras.add(act);
    }

    public void removeActivity(Activity act) {
        cameras.remove(act);
    }


}
