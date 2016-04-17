package community.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.ui.fragments.FollowedTopicFragment;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import community.fragment.TopicFragment;

/**
 * 用户关注的话题列表
 */
@EActivity(R.layout.activity_followed_topic)
public class FollowedTopicActivity extends AppCompatActivity {
    private TopicFragment fragment;

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("话题");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new TopicFragment();
        ft.add(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }


    private Fragment createFragment(Intent intent) {
        String uid = intent.getStringExtra(Constants.USER_ID_KEY);
        return FollowedTopicFragment.newFollowedTopicFragment(uid);
    }
}
