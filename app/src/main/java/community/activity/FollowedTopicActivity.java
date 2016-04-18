package community.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import community.fragment.FollowedTopicFragment;


/**
 * 用户关注的话题列表
 */
@EActivity(R.layout.activity_followed_topic)
public class FollowedTopicActivity extends AppCompatActivity {
    @Extra
    public String uid;
    private FollowedTopicFragment fragment;

    @AfterViews
    public void initView() {
        setTitle("话题");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new FollowedTopicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        fragment.setArguments(bundle);
        ft.add(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
