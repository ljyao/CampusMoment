package community.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.umeng.comm.core.beans.Topic;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.providable.FeedPrvdr;
import community.providable.NetLoaderListener;
import community.providable.TopicInfoPrvdr;

/**
 * Created by shine on 16-2-18.
 */
@EActivity(R.layout.activity_topic_detail)
public class TopicDetailActivity extends AppCompatActivity {
    @Extra
    protected Topic topic;
    private FeedFragment fragment;
    private TopicInfoPrvdr topicInfoPrvdr;
    private OnClickFollowTopicListener followTopicListener = new OnClickFollowTopicListener() {
        @Override
        public void onFollowTopic(Topic topic) {
            TopicDetailActivity.this.topic = topic;
            topicInfoPrvdr.followTopic(topic, followTopicCallBack);
        }

        @Override
        public void onUnFollowTopic(Topic topic) {
            TopicDetailActivity.this.topic = topic;
            topicInfoPrvdr.cancelFollowTopic(topic, followTopicCallBack);
        }
    };
    private NetLoaderListener<Boolean> followTopicCallBack = new NetLoaderListener<Boolean>() {
        @Override
        public void onComplete(boolean statue, Boolean result) {
            fragment.setTopic(topic, followTopicListener);
        }
    };

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(topic.name);
        getTopicInfo(topic.id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = FeedFragment_.builder().feedType(FeedPrvdr.FeedType.TopicFeed).topic(topic).build();
        ft.add(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    public void getTopicInfo(String topicId) {
        topicInfoPrvdr = new TopicInfoPrvdr();
        topicInfoPrvdr.fetchTopicWithId(topicId, new NetLoaderListener<Topic>() {
            @Override
            public void onComplete(boolean statue, Topic result) {
                topic = result;
                fragment.setTopic(topic, followTopicListener);
            }
        });
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

    public interface OnClickFollowTopicListener {
        void onFollowTopic(Topic topic);

        void onUnFollowTopic(Topic topic);

    }
}
