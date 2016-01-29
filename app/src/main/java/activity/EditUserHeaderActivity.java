package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import choosephoto.activity.PhotoWallActivity;
import community.providable.UserPrvdr;
import helper.util.MyImageUtils;
import imagezoom.ImageViewTouch;

/**
 * Created by ljy on 15/12/29.
 */
@EActivity(R.layout.activity_edti_userheader)
public class EditUserHeaderActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DETI_USERHEADE = 1;
    @ViewById(R.id.user_header)
    public SimpleDraweeView userHeader;
    @ViewById(R.id.choosephoto)
    public FloatingActionButton choosePhoto;
    @ViewById(R.id.choosephoto_txt)
    public TextView choosePhotoTv;
    @ViewById(R.id.edit_image_view)
    public ImageViewTouch editViewTouch;
    private CommUser user;
    private boolean isEdit = false;
    private String newPath;
    private Bitmap newPhotoBitmap;

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("修改头像");
        changeStatue(false);
    }

    @Click(R.id.choosephoto)
    public void onClickChoosePhoto() {
        if (!isEdit) {
            Intent intent = new Intent(this, PhotoWallActivity.class);
            intent.putExtra("SingleChoose", true);
            startActivityForResult(intent, REQUEST_CODE_DETI_USERHEADE);
        } else {
            confirmBtn();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DETI_USERHEADE:
                changeStatue(true);
                setNewHead(data);
                break;
        }
    }

    private void setUserHeader() {
        if (newPhotoBitmap != null) {
            userHeader.setImageBitmap(newPhotoBitmap);
        }
        user = CommConfig.getConfig().loginedUser;
        int pos = user.iconUrl.indexOf("@");
        String userIcon = user.iconUrl;
        if (pos > 0) {
            userIcon = user.iconUrl.substring(0, pos);
        }
        userHeader.setImageURI(Uri.parse(userIcon));
    }

    public void confirmBtn() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("上传中...");
        dialog.show();

        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                /*editViewTouch.setDrawingCacheEnabled(true);
                editViewTouch.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                editViewTouch.buildDrawingCache();*/
                newPhotoBitmap = editViewTouch.getDrawingCache();
                UserPrvdr userPrvdr = new UserPrvdr();
                userPrvdr.updateUserProtrait(newPhotoBitmap, new UserPrvdr.UserListener() {
                    @Override
                    public void onComplete(CommUser user) {
                        dialog.dismiss();
                        changeStatue(false);
                    }
                }, EditUserHeaderActivity.this);
            }
        });
        final Bitmap bitmap = BitmapFactory.decodeFile(newPath);
        UserPrvdr userPrvdr = new UserPrvdr();
        userPrvdr.updateUserProtrait(bitmap, new UserPrvdr.UserListener() {
            @Override
            public void onComplete(CommUser user) {
                Toast.makeText(EditUserHeaderActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                bitmap.recycle();
                changeStatue(false);

            }
        }, EditUserHeaderActivity.this);

    }

    private void changeStatue(boolean statue) {
        isEdit = statue;
        if (statue) {
            choosePhoto.setImageResource(R.drawable.camera_edit_check);
            choosePhotoTv.setText("确认");
            userHeader.setVisibility(View.GONE);
            editViewTouch.setVisibility(View.VISIBLE);
        } else {
            setUserHeader();
            userHeader.setVisibility(View.VISIBLE);
            editViewTouch.setVisibility(View.GONE);
            choosePhotoTv.setText("选择照片");
            choosePhoto.setImageResource(R.drawable.compose_photo_photograph);
        }
    }

    private void setNewHead(Intent intent) {
        if (intent == null) {
            return;
        }
        int code = intent.getIntExtra("code", 101);
        if (code == 100) {
            ArrayList<String> paths = intent.getStringArrayListExtra("paths");
            newPath = paths.get(0);
            MyImageUtils.setImagePath(editViewTouch, newPath);
        }
    }
}
