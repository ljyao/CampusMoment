package activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.providable.FeedPrvdr;
import community.providable.NetLoaderListener;
import community.providable.UserPrvdr;

/**
 * Created by ljy on 15/12/25.
 */
@EActivity(R.layout.activity_me)
public class UserDetailActivity extends AppCompatActivity {
    @ViewById(R.id.user_header)
    public SimpleDraweeView userHeader;
    @ViewById(R.id.collapsing_toolbar_layout)
    public CollapsingToolbarLayout mCollapsingToolbarLayout;
    @ViewById(R.id.toolbar)
    public Toolbar mToolbar;
    @ViewById(R.id.appbar)
    public AppBarLayout mAppBarLayout;
    @Extra
    public CommUser user;
    private FeedFragment feedFragment;
    private UserPrvdr userPrvdr;

    @AfterViews
    public void initView() {
        userPrvdr = new UserPrvdr();
        if (user == null) {
            user = CommConfig.getConfig().loginedUser;
        }

        setData(user);
        refreshUser(user);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        feedFragment = FeedFragment_.builder().feedType(FeedPrvdr.FeedType.UserFeed).userId(user.id).build();
        ft.replace(R.id.fragment, feedFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    public void setData(CommUser user) {
        setUserHeader();
        setToolBar();
    }

    private void refreshUser(final CommUser user) {
        userPrvdr.getUserInfo(user, new NetLoaderListener<CommUser>() {
            @Override
            public void onComplete(boolean statue, CommUser result) {
                if (result != null) {
                    UserDetailActivity.this.user = result;
                    setData(user);
                }
            }
        });
    }

    public void followUser() {
        NetLoaderListener<Boolean> listener = new NetLoaderListener<Boolean>() {
            @Override
            public void onComplete(boolean statue, final Boolean result) {
                Worker.postMain(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }
        };
        if (user.isFollowed) {
            userPrvdr.cancelFollowUser(user, listener);
        } else {
            userPrvdr.followUser(user, listener);
        }
    }

    private void setUserHeader() {
        int pos = user.iconUrl.indexOf("@");
        String userIcon = user.iconUrl;
        if (pos > 0) {
            userIcon = user.iconUrl.substring(0, pos);
        }
        userHeader.setImageURI(Uri.parse(userIcon));
    }

    private void setToolBar() {
        setSupportActionBar(mToolbar);
        mCollapsingToolbarLayout.setTitle(user.name);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_me, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_user_info:
                Intent intent = new Intent(this, SetUserInfoActivity_.class);
                startActivity(intent);
                break;
            case R.id.menu_follow_user:
                followUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (user.isFollowed) {
            menu.findItem(R.id.menu_follow_user).setTitle("取消关注");
        } else {
            menu.findItem(R.id.menu_follow_user).setTitle("关注");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userHeader != null) {
            setUserHeader();
        }
        if (feedFragment != null) {
            feedFragment.refreshFeed();
        }
    }
}
