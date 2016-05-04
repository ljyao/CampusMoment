package community.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.ui.fragments.FollowedTopicFragment;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;


/**
 * 用户关注的话题列表
 */
@EActivity(R.layout.activity_followed_topic)
public class FollowedTopicActivity extends AppCompatActivity {
    @Extra
    public CommUser user;
    private FollowedTopicFragment fragment;

    @AfterViews
    public void initView() {
        setTitle("我的话题");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = FollowedTopicFragment.newFollowedTopicFragment(user.id);
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
