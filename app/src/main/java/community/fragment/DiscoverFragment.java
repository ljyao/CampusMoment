package community.fragment;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

import com.umeng.comm.core.beans.Topic;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import community.providable.NetLoaderListener;
import community.providable.TopicPrvdr;
import convenientbanner.ConvenientBanner;
import convenientbanner.holder.CBViewHolderCreator;
import convenientbanner.holder.ImageHolder;

/**
 * Created by ljy on 15/12/25.
 */
@EFragment(R.layout.discover_fragment)
public class DiscoverFragment extends Fragment {
    public TopicPrvdr mTopicPrvdr;
    @ViewById(R.id.convenientBanner)
    public ConvenientBanner convenientBanner;
    @ViewById(R.id.topic_one_tv)
    protected TextView topicOneTv;
    @ViewById(R.id.topic_two_tv)
    protected TextView topicTwoTv;
    @ViewById(R.id.topic_three_tv)
    protected TextView topicThreeTv;
    @ViewById(R.id.topic_hot_tv)
    protected TextView topicHotTv;

    @AfterViews
    public void initView() {
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
            List<Uri> topicImages = new ArrayList();
            String[] topicNames = new String[3];
            int i = 0;
            for (Topic topic : result) {
                if (i < 3) {
                    topicNames[i] = topic.name;
                }
                i++;
                if (!TextUtils.isEmpty(topic.icon) && !topic.icon.equals("null") && topicImages.size() < 6) {
                    int index = topic.icon.indexOf("@");
                    String iconPath = topic.icon.substring(0, index);
                    topicImages.add(Uri.parse(iconPath));
                }
            }
            CBViewHolderCreator<ImageHolder> creator = new CBViewHolderCreator() {

                @Override
                public ImageHolder createHolder() {
                    return new ImageHolder();
                }
            };
            convenientBanner.setPages(creator, topicImages)
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                    //设置指示器的方向
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                    .setCanLoop(true);

            topicOneTv.setText(topicNames[0]);
            topicTwoTv.setText(topicNames[1]);
            topicThreeTv.setText(topicNames[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
