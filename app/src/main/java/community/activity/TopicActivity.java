package community.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.umeng.comm.core.beans.Topic;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.providable.FeedPrvdr;

/**
 * Created by shine on 16-2-18.
 */
@EActivity(R.layout.activity_topic)
public class TopicActivity extends AppCompatActivity {
    @Extra
    protected Topic topic;

    @AfterViews
    public void initView() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FeedFragment fragment = FeedFragment_.builder().build();
        fragment.setFeedType(FeedPrvdr.FeedType.TopicFeed, topic);
        ft.add(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

}
