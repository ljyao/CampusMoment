package community.fragment;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.umeng.comm.core.beans.Topic;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import community.providable.NetLoaderListener;
import community.providable.TopicPrvdr;

/**
 * Created by ljy on 15/12/25.
 */
@EFragment(R.layout.discover_fragment)
public class DiscoverFragment extends Fragment {
    public TopicPrvdr mTopicPrvdr;
    @ViewById(R.id.topic_one_tv)
    protected TextView topicOneTv;
    @ViewById(R.id.topic_two_tv)
    protected TextView topicTwoTv;
    @ViewById(R.id.topic_three_tv)
    protected TextView topicThreeTv;
    @ViewById(R.id.topic_hot_tv)
    protected TextView topicHotTv;

    @AfterViews
    public void initData() {
        mTopicPrvdr = new TopicPrvdr();
        mTopicPrvdr.loadTopicList(new NetLoaderListener<List<Topic>>() {
            @Override
            public void onComplete(boolean statue, List<Topic> result) {
                setData(result);
            }
        });
    }

    public void setData(List<Topic> result) {
        try {
            String[] topicNames = new String[3];
            int i = 0;
            for (Topic topic : result) {
                topicNames[i] = topic.name;
                if (i == 2) {
                    break;
                }
                i++;
            }
            topicOneTv.setText(topicNames[0]);
            topicTwoTv.setText(topicNames[1]);
            topicThreeTv.setText(topicNames[2]);
        } catch (Exception e) {
        }
    }
}
