package community.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.umeng.comm.core.beans.Topic;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import community.activity.HotTopicActivity_;
import community.activity.TopicActivity_;
import community.providable.NetLoaderListener;
import community.providable.TopicPrvdr;
import convenientbanner.ConvenientBanner;
import convenientbanner.holder.CBViewHolderCreator;
import convenientbanner.holder.ImageHolder;
import convenientbanner.listener.OnItemClickListener;

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
    private View.OnClickListener topicTvListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (v == topicHotTv) {
                    HotTopicActivity_.intent(getContext()).start();
                } else {
                    Topic topic = (Topic) v.getTag();
                    if (topic == null)
                        return;
                    TopicActivity_.intent(getActivity()).topic(topic).start();
                }
            } catch (Exception e) {
            }

        }
    };

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
            final List<Uri> topicImages = new ArrayList();
            final List<Topic> topicBanners = new ArrayList<>();
            Topic[] topics = new Topic[3];
            int i = 0;
            for (Topic topic : result) {
                if (i < 3) {
                    topics[i] = topic;
                }
                i++;
                if (!TextUtils.isEmpty(topic.icon) && !topic.icon.equals("null") && topicImages.size() < 6) {
                    int index = topic.icon.indexOf("@");
                    String iconPath = topic.icon.substring(0, index);
                    topicImages.add(Uri.parse(iconPath));
                    topicBanners.add(topic);
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
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            TopicActivity_.intent(getActivity()).topic(topicBanners.get(position)).start();
                        }
                    })
                    .setCanLoop(topicImages.size() > 1 ? true : false);

            topicOneTv.setText(topics[0].name);
            topicOneTv.setOnClickListener(topicTvListener);
            topicOneTv.setTag(topics[0]);
            topicTwoTv.setText(topics[1].name);
            topicTwoTv.setOnClickListener(topicTvListener);
            topicTwoTv.setTag(topics[1]);
            topicThreeTv.setText(topics[2].name);
            topicThreeTv.setOnClickListener(topicTvListener);
            topicThreeTv.setTag(topics[2]);

            topicHotTv.setOnClickListener(topicTvListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (convenientBanner != null) {
            convenientBanner.stopTurning();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (convenientBanner != null) {
            convenientBanner.startTurning();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}
