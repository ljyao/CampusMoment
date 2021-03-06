package community.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.comm.ui.imagepicker.util.BroadcastUtils;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.List;

import community.activity.FeedDetailActivity;
import community.activity.TopicDetailActivity;
import community.adapter.FeedAdapter;
import community.providable.FeedPrvdr;
import community.providable.NetLoaderListener;
import fragment.RefreshRecycleFragment;
import helper.common_util.ScreenUtils;


/**
 * Created by ljy on 15/12/16.
 */
@EFragment
public class FeedFragment extends RefreshRecycleFragment<FeedAdapter> {
    @FragmentArg
    public FeedPrvdr.FeedType feedType;
    @FragmentArg
    public Topic topic;
    @FragmentArg
    public String userId;
    @FragmentArg
    public Location location;
    private FeedPrvdr feedPrvdr;
    private LinearLayoutManager layoutManager;

    private FeedListListener feedListListener = new FeedListListener() {
        @Override
        public void onCommentClickListener(FeedItem mFeedItem) {
            //先进入feed详情页面，再弹出评论编辑键盘
            Intent intent = new Intent(getActivity(), FeedDetailActivity.class);
            intent.putExtra(Constants.TAG_FEED, mFeedItem);
            intent.putExtra(Constants.TAG_IS_COMMENT, true);
            intent.putExtra(Constants.TAG_IS_SCROLL, true);
            getActivity().startActivity(intent);
        }

        @Override
        public void onShowFeedDetail(FeedItem feedItem) {

            if (feedItem != null && feedItem.status >= FeedItem.STATUS_SPAM
                    && feedItem.category == FeedItem.CATEGORY.FAVORITES && feedItem.status != FeedItem.STATUS_LOCK) {
                ToastMsg.showShortMsgByResName("umeng_comm_feed_spam_deleted");
                return;
            }
            Intent intent = new Intent(getActivity(), FeedDetailActivity.class);
            intent.putExtra(Constants.TAG_FEED, feedItem);
            startActivity(intent);
        }
    };
    private boolean isHideRefreshLayout = false;
    private TopicDetailActivity.OnClickFollowTopicListener followTopicListener;
    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveFeed(android.content.Intent intent) {
            FeedItem feedItem = getFeed(intent);
            if (feedItem == null) {
                return;
            }
            refreshFeed();
        }
    };

    public FeedFragment() {

    }

    @AfterViews
    public void initView() {
        BroadcastUtils.registerFeedBroadcast(getActivity(), mReceiver);
        if (isHideRefreshLayout) {
            refreshLayout.setVisibility(View.GONE);
        }
        if (feedType == null)
            feedType = FeedPrvdr.FeedType.FollowFeed;
        feedPrvdr = new FeedPrvdr(this.feedType);
        adapter = new FeedAdapter(feedType);
        if (feedType == FeedPrvdr.FeedType.TopicFeed) {
            feedPrvdr.setTopicId(topic.id);
            adapter.setTopic(topic, followTopicListener);
        } else if (feedType == FeedPrvdr.FeedType.LocationFeed) {
            feedPrvdr.setLocation(location);
        } else if (feedType == FeedPrvdr.FeedType.UserFeed) {
            feedPrvdr.setUserId(userId);
        }
        List<FeedItem> feeds = feedPrvdr.getLocalData();
        adapter.update(feeds);
        adapter.setListener(feedListListener);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new FeedDecoration());
        refreshFeed();
    }

    public void refreshFeed() {
        layoutManager.scrollToPosition(0);
        refreshLayout.setRefreshing(true);
        onRefreshing();
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        layoutManager = new LinearLayoutManager(getContext());
        return layoutManager;
    }

    @Override
    protected void onRefreshing() {
        feedPrvdr.getFirstPageData(new NetLoaderListener<List<FeedItem>>() {
            @Override
            public void onComplete(boolean statue, List<FeedItem> result) {
                setRefreshState(false);
                if (result != null && result.size() > 0)
                    adapter.update(result);
            }
        });
    }

    @Override
    protected void onLoadMore() {
        feedPrvdr.fetchNextPageData(new NetLoaderListener<List<FeedItem>>() {
            @Override
            public void onComplete(boolean statue, List<FeedItem> result) {
                setRefreshState(false);
                if (statue)
                    adapter.append(result);
            }
        });
    }

    private void setRefreshState(final boolean state) {
        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(state);
            }
        }, 500);
    }

    public void setTopic(Topic topic, TopicDetailActivity.OnClickFollowTopicListener followTopicListener) {
        this.topic = topic;
        this.followTopicListener = followTopicListener;
        adapter.setTopic(topic, followTopicListener);
        adapter.notifyItemChanged(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void hideRefresh() {
        isHideRefreshLayout = true;
        if (isHideRefreshLayout && refreshLayout != null) {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public interface FeedListListener {
        void onCommentClickListener(FeedItem mFeedItem);

        void onShowFeedDetail(FeedItem mFeedItem);
    }

    static class FeedDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, ScreenUtils.dp2px(1, parent.getContext()));
        }
    }
}
