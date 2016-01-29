package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import community.providable.UserPrvdr;
import imagezoom.ImageViewTouch;

/**
 * Created by ljy on 15/12/29.
 */
@EActivity(R.layout.edit_photo_activity)
public class EditPhotoActivity extends AppCompatActivity {
    @ViewById(R.id.crop_image)
    public ImageViewTouch imageView;
    private int bgColor = Color.WHITE;
    private String path;

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Click(R.id.crop_image)
    public void onClickImage() {
        if (bgColor == Color.WHITE) {
            bgColor = Color.BLACK;
            imageView.setBackgroundColor(Color.BLACK);
        } else {
            bgColor = Color.WHITE;
            imageView.setBackgroundColor(Color.WHITE);
        }
    }

    @Click(R.id.confirm_btn)
    public void onclickConfirmBtn() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("上传中...");
        dialog.show();

        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                imageView.setDrawingCacheEnabled(true);
                imageView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();
                UserPrvdr userPrvdr = new UserPrvdr();
                userPrvdr.updateUserProtrait(bitmap, new UserPrvdr.UserListener() {
                    @Override
                    public void onComplete(CommUser user) {
                        dialog.dismiss();
                        Intent intent = new Intent(EditPhotoActivity.this, EditUserHeaderActivity_.class);
                        startActivity(intent);
                        EditPhotoActivity.this.finish();
                    }
                }, EditPhotoActivity.this);
            }
        });


    }

    private void initData() {
        Intent intent = getIntent();
        int code = intent.getIntExtra("code", 101);
        if (code == 100) {
            ArrayList<String> paths = intent.getStringArrayListExtra("paths");
            path = paths.get(0);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        }
    }
}
