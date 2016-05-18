package community.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import community.fragment.FollowedUserFragment;
import community.fragment.FollowedUserFragment_;

/**
 * Created by Shine on 2016/3/21.
 */
@EActivity(R.layout.activity_followed_user)
public class FollowedUserActivity extends AppCompatActivity {
    @Extra
    public String userId;
    @Extra
    public FollowedUserFragment.UserListType type;
    private FollowedUserFragment fragment;

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (type == FollowedUserFragment.UserListType.followed)
            setTitle("关注");
        else if (type == FollowedUserFragment.UserListType.fans)
            setTitle("粉丝");
        else if (type == FollowedUserFragment.UserListType.Recommended)
            setTitle("推荐好友");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = FollowedUserFragment_.builder().mUserId(userId).type(type).build();
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
