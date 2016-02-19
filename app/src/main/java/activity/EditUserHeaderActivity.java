package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import choosephoto.activity.PhotoWallActivity;
import community.providable.UserPrvdr;
import helper.AppConstants;
import helper.util.ImageUtils;
import helper.util.MyImageUtils;
import imagezoom.ImageViewTouch;
import imagezoom.ImageViewTouchBase;

/**
 * Created by ljy on 15/12/29.
 */
@EActivity(R.layout.activity_edti_userheader)
public class EditUserHeaderActivity extends AppCompatActivity {
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


    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        setTitle("修改头像");
        changeStatue(false);
    }

    @Click(R.id.choosephoto)
    public void onClickChoosePhoto() {
        if (!isEdit) {
            Intent intent = new Intent(this, PhotoWallActivity.class);
            intent.putExtra("SingleChoose", true);
            intent.putExtra("from", AppConstants.REQUEST_EDIT_USERHEAD);
            startActivityForResult(intent, AppConstants.REQUEST_EDIT_USERHEAD);
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
            case AppConstants.REQUEST_EDIT_USERHEAD:
                if (resultCode == RESULT_OK) {
                    changeStatue(true);
                    setNewHead(data);
                }
                break;
        }
    }

    private void setUserHeader() {

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
                final Bitmap newPhotoBitmap = ImageUtils.getBitmapFromView(editViewTouch);
                UserPrvdr userPrvdr = new UserPrvdr();
                userPrvdr.updateUserProtrait(newPhotoBitmap, new UserPrvdr.UserListener() {
                    @Override
                    public void onComplete(CommUser user) {
                        Toast.makeText(EditUserHeaderActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        newPhotoBitmap.recycle();
                        changeStatue(false);
                    }
                }, EditUserHeaderActivity.this);
            }
        });

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
        newPath = intent.getData().getPath();
        MyImageUtils.setImagePath(editViewTouch, newPath);

    }
}
