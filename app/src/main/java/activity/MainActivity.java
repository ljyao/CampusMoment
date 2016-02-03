package activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

import camera.ui.CameraActivity_;
import choosephoto.activity.PhotoWallActivity;
import community.fragment.DiscoverFragment;
import community.fragment.DiscoverFragment_;
import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.fragment.MeFragment;
import community.fragment.MeFragment_;
import community.fragment.MessageFragment;
import community.fragment.MessageFragment_;
import community.providable.FeedPrvdr;


/**
 * Created by ljy on 15/12/16.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    public final static int FEED_FRAGMENT_ID = 0;
    public final static int MESSAGE_FRAGMENT_ID = 1;
    public final static int DISCOVER_FRAGMENT_ID = 2;
    public final static int AT_FRAGMENT_ID = 3;
    private static final int REQUEST_CODE_CHOOSEPHOTO = 0;
    @ViewById(R.id.action_feed_img)
    public ImageView mActionFeed;
    @ViewById(R.id.action_message_img)
    public ImageView mActionMessage;
    @ViewById(R.id.action_add_btn)
    public ImageButton mActionAdd;
    @ViewById(R.id.action_discover_img)
    public ImageView mActionDiscover;
    @ViewById(R.id.action_me_img)
    public ImageView mActionMe;
    @ViewById(R.id.add_feed_rl)
    public RelativeLayout addFeedLayout;
    @ViewById(R.id.toolbar)
    public Toolbar mToolbar;
    public Map<Integer, Fragment> fragments;
    private Fragment currentFragment = null;

    @AfterViews
    public void initViews() {
        setSupportActionBar(mToolbar);
        chooseAction(FEED_FRAGMENT_ID);
        initFragment();
        showFragment(FEED_FRAGMENT_ID);
    }


    @Click({R.id.action_feed, R.id.action_feed_img})
    public void onClickFeed() {
        chooseAction(FEED_FRAGMENT_ID);
        showFragment(FEED_FRAGMENT_ID);
    }

    @Click({R.id.action_message, R.id.action_message_img})
    public void onClickMessage() {
        chooseAction(MESSAGE_FRAGMENT_ID);
        showFragment(MESSAGE_FRAGMENT_ID);
    }

    @Click(R.id.action_add_btn)
    public void onClickAdd(View v) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.popup_bottom_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mToolbar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        addFeedLayout.setAnimation(anim);
        addFeedLayout.setVisibility(View.VISIBLE);
    }

    @Click({R.id.add_txt_feed, R.id.add_photo_feed, R.id.add_carmen_feed})
    public void onCliceAdd(View v) {
        switch (v.getId()) {
            case R.id.add_txt_feed:
                break;
            case R.id.add_photo_feed:
                Intent intent = new Intent(this, PhotoWallActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CHOOSEPHOTO);
                break;
            case R.id.add_carmen_feed:
                CameraActivity_.intent(this).start();
                break;
        }
        mToolbar.setVisibility(View.VISIBLE);
        addFeedLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Click({R.id.cancel_add_feed, R.id.add_feed_rl})
    public void onCancelAddFeed() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.popup_bottom_out);
        addFeedLayout.setAnimation(anim);
        mToolbar.setVisibility(View.VISIBLE);
        addFeedLayout.setVisibility(View.GONE);
    }

    @Click({R.id.action_discover, R.id.action_discover_img})
    public void onClickDiscover() {
        chooseAction(DISCOVER_FRAGMENT_ID);
        showFragment(DISCOVER_FRAGMENT_ID);
    }

    @Click(R.id.action_me)
    public void onClickMe() {
        chooseAction(AT_FRAGMENT_ID);
        showFragment(AT_FRAGMENT_ID);
    }

    public void chooseAction(int id) {
        mActionFeed.setBackgroundResource(R.drawable.icon_feed_n);
        mActionMessage.setBackgroundResource(R.drawable.icon_message_n);
        mActionDiscover.setBackgroundResource(R.drawable.icon_discover_n);
        mActionMe.setBackgroundResource(R.drawable.icon_me_n);
        switch (id) {
            case FEED_FRAGMENT_ID:
                mActionFeed.setBackgroundResource(R.drawable.icon_feed_d);
                break;
            case MESSAGE_FRAGMENT_ID:
                mActionMessage.setBackgroundResource(R.drawable.icon_message_d);
                break;
            case DISCOVER_FRAGMENT_ID:
                mActionDiscover.setBackgroundResource(R.drawable.icon_discover_d);
                break;
            case AT_FRAGMENT_ID:
                mActionMe.setBackgroundResource(R.drawable.icon_me_d);
                break;
        }
    }

    public void showFragment(int i) {
        Fragment nextFragment = fragments.get(i);
        if (currentFragment == nextFragment) {
            if (FEED_FRAGMENT_ID == i) {
                FeedFragment feedFragment = (FeedFragment) nextFragment;
                feedFragment.refreshFeed();
            }
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

    public void initFragment() {
        fragments = new HashMap<>(5);
        DiscoverFragment discoverFragment = DiscoverFragment_.builder().build();
        FeedFragment feedFragment = FeedFragment_.builder().build();
        feedFragment.setFeedType(FeedPrvdr.FeedType.FollowFeed);
        MeFragment meFragment = MeFragment_.builder().build();
        MessageFragment messageFragment = MessageFragment_.builder().build();
        fragments.put(FEED_FRAGMENT_ID, feedFragment);
        fragments.put(MESSAGE_FRAGMENT_ID, messageFragment);
        fragments.put(DISCOVER_FRAGMENT_ID, discoverFragment);
        fragments.put(AT_FRAGMENT_ID, meFragment);
    }

    // 重写返回键
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (addFeedLayout.getVisibility() == View.VISIBLE) {
                onCancelAddFeed();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }
}
