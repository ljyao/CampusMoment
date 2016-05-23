package activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import community.providable.NetLoaderListener;
import community.providable.UserPrvdr;
import provider.ToastUtil;

/**
 * Created by ljy on 15/12/29.
 */
@EActivity(R.layout.activity_userinfo_set)
public class SetUserInfoActivity extends AppCompatActivity {
    @ViewById(R.id.user_icon)
    public SimpleDraweeView userHeader;
    @ViewById(R.id.nick_name)
    public EditText nickName;
    @ViewById(R.id.user_sex)
    public EditText userSex;
    private CommUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_set);
        setTitle("修改资料");
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @AfterViews
    public void initView() {
        setUserHeader();
        if (!TextUtils.isEmpty(user.name)) {
            nickName.setText(user.name);
        }
        if (user.gender != null) {
            userSex.setText(user.gender == CommUser.Gender.MALE ? "男" : "女");
        }
    }

    private void setUserHeader() {
        if (userHeader == null)
            return;
        user = CommConfig.getConfig().loginedUser;
        int pos = user.iconUrl.indexOf("@");
        String userIcon = user.iconUrl;
        if (pos > 0) {
            userIcon = user.iconUrl.substring(0, pos);
        }
        userHeader.setImageURI(Uri.parse(userIcon));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_user_info, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserHeader();
    }

    @Click(R.id.user_icon)
    public void setUserIcon() {
        EditUserHeaderActivity_.intent(this).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_userinfo_save:
                updateUser();
                break;
            default:
                break;
        }
        return false;
    }

    private void updateUser() {
        UserPrvdr userPrvdr = new UserPrvdr();
        user.name = nickName.getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("保存中...");
        progressDialog.show();
        userPrvdr.cancelFollowUser(user, new NetLoaderListener<Boolean>() {
            @Override
            public void onComplete(boolean statue, Boolean result) {
                ToastUtil.showLongToast(SetUserInfoActivity.this, "保存成功！");
                progressDialog.dismiss();
                SetUserInfoActivity.this.finish();
            }
        });
    }
}
