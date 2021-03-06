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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import community.activity.FeedListActivity_;
import community.activity.FollowedUserActivity_;
import community.activity.HotTopicActivity_;
import community.activity.TopicDetailActivity_;
import community.providable.FeedPrvdr;
import community.providable.NetLoaderListener;
import community.providable.TopicPrvdr;
import widget.convenientbanner.ConvenientBanner;
import widget.convenientbanner.holder.CBViewHolderCreator;
import widget.convenientbanner.holder.ImageHolder;
import widget.convenientbanner.listener.OnItemClickListener;

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
                    TopicDetailActivity_.intent(getActivity()).topic(topic).start();
                }
            } catch (Exception e) {
            }

        }
    };

    @AfterViews
    public void initView() {
        mTopicPrvdr = new TopicPrvdr();
        List<Topic> topics = mTopicPrvdr.loadTopicList(new NetLoaderListener<List<Topic>>() {
            @Override
            public void onComplete(boolean statue, List<Topic> result) {
                setData(result);
            }
        });
        setData(topics);
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
                            TopicDetailActivity_.intent(getActivity()).topic(topicBanners.get(position)).start();
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

    @Click(R.id.hot_feed)
    public void onClickHotFeed() {
        FeedListActivity_.intent(this).title("热门微博")
                .feedType(FeedPrvdr.FeedType.HotestFeed).start();
    }

    @Click(R.id.near_feed)
    public void onClickNearFeed() {
        FeedListActivity_.intent(this).title("周边")
                .feedType(FeedPrvdr.FeedType.NearFeed).start();
    }

    @Click(R.id.recommended_user)
    public void onClickRecommendedUser() {
        FollowedUserActivity_.intent(this)
                .type(FollowedUserFragment.UserListType.Recommended).start();
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
