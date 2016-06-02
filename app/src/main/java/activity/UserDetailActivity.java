package activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import community.activity.FollowedUserActivity_;
import community.fragment.AlbumsFragment;
import community.fragment.AlbumsFragment_;
import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.fragment.FollowedUserFragment;
import community.fragment.TopicListFragment;
import community.fragment.TopicListFragment_;
import community.providable.FeedPrvdr;
import community.providable.NetLoaderListener;
import community.providable.UserPrvdr;
import model.User;

/**
 * Created by ljy on 15/12/25.
 */
@EActivity(R.layout.activity_me)
public class UserDetailActivity extends AppCompatActivity {
    private static final int FEEd = 0;
    private static final int TOPIC = 1;
    private static final int ALBUM = 2;
    @ViewById(R.id.user_header)
    public SimpleDraweeView userHeader;
    @ViewById(R.id.collapsing_toolbar_layout)
    public CollapsingToolbarLayout mCollapsingToolbarLayout;
    @ViewById(R.id.toolbar)
    public Toolbar mToolbar;
    @ViewById(R.id.appbar)
    public AppBarLayout mAppBarLayout;
    @ViewById(R.id.user_feed)
    public TextView userFeed;
    @ViewById(R.id.user_topic)
    public TextView userTopic;
    @ViewById(R.id.user_album)
    public TextView userAlbum;
    @Extra
    public CommUser user;
    private UserPrvdr userPrvdr;
    private List<Fragment> fragments;
    private Fragment currentFragment;
    private boolean isMe = false;

    @AfterViews
    public void initView() {
        userPrvdr = new UserPrvdr();
        if (user == null) {
            isMe = true;
            user = CommConfig.getConfig().loginedUser;
        }
        setData(user);
        refreshUser(user);
        initFragments();
        userFeed.setTextColor(getResources().getColor(R.color.colorPrimary));
        showFragment(FEEd);
    }

    private void initFragments() {
        FeedFragment feedFragment = FeedFragment_.builder().feedType(FeedPrvdr.FeedType.UserFeed).userId(user.id).build();
        AlbumsFragment albumsFragment = AlbumsFragment_.builder().user(user).build();
        TopicListFragment followedTopicFragment = TopicListFragment_.builder().user(user).build();
        fragments = new ArrayList<>();
        fragments.add(FEEd, feedFragment);
        fragments.add(TOPIC, followedTopicFragment);
        fragments.add(ALBUM, albumsFragment);
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
            case R.id.menu_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent intent=new Intent(this,LoginActivity.class);
        User.logout(this);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem followUserMenu = menu.findItem(R.id.menu_follow_user);
        MenuItem logoutMenu=menu.findItem(R.id.menu_logout);
        CommUser me = CommConfig.getConfig().loginedUser;
        if (isMe) {
            followUserMenu.setVisible(false);
            logoutMenu.setVisible(true);
        } else {
            logoutMenu.setVisible(false);
            followUserMenu.setVisible(true);
            if (user.isFollowed) {
                followUserMenu.setTitle("取消关注");
            } else {
                followUserMenu.setTitle("关注");
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void showFragment(int i) {
        Fragment nextFragment = fragments.get(i);
        if (currentFragment != null
                && currentFragment == nextFragment) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (nextFragment.isAdded()) {
            ft.show(nextFragment);
        } else {
            ft.add(R.id.fragment, nextFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
        currentFragment = nextFragment;
    }

    @Click({R.id.user_topic, R.id.user_feed, R.id.user_album})
    public void onClickChooseFragment(View view) {
        int chooseItem = FEEd;
        switch (view.getId()) {
            case R.id.user_topic:
                chooseItem = TOPIC;
                break;
            case R.id.user_feed:
                chooseItem = FEEd;
                break;
            case R.id.user_album:
                chooseItem = ALBUM;
                break;
        }
        userAlbum.setTextColor(Color.BLACK);
        userFeed.setTextColor(Color.BLACK);
        userTopic.setTextColor(Color.BLACK);
        ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
        showFragment(chooseItem);
    }

    @Click(R.id.user_follow)
    public void onClickFollowUser() {
        FollowedUserActivity_.intent(this).userId(user.id).
                type(FollowedUserFragment.UserListType.followed).start();
    }

    @Click(R.id.user_fans)
    public void onClickFollowUserFans() {
        FollowedUserActivity_.intent(this).userId(user.id).
                type(FollowedUserFragment.UserListType.fans).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userHeader != null) {
            setUserHeader();
        }
    }
}
