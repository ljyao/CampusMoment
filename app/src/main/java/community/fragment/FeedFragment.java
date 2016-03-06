package community.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.utils.ToastMsg;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.List;

import activity.FeedDetailActivity;
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
    private FeedPrvdr feedPrvdr;
    private FeedPrvdr.FeedType mFeedType = FeedPrvdr.FeedType.FollowFeed;
    private LinearLayoutManager layoutManager;
    private String userOrTopicId = "";

    private FeedListListener feedListListener = new FeedListListener() {
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

    public FeedFragment() {
        feedPrvdr = new FeedPrvdr(mFeedType);
        adapter = new FeedAdapter();
        adapter.setListener(feedListListener);
    }

    @AfterViews
    public void initView() {
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
                adapter.update(result);
            }
        }, userOrTopicId);
    }

    @Override
    protected void onLoadMore() {
        feedPrvdr.fetchNextPageData(new NetLoaderListener<List<FeedItem>>() {
            @Override
            public void onComplete(boolean statue, List<FeedItem> result) {
                setRefreshState(false);
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


    public void setFeedType(FeedPrvdr.FeedType feedType, Topic topic) {
        if (topic != null) {
            userOrTopicId = topic.id;
            adapter.setTopic(topic);
        }
        this.mFeedType = feedType;
        feedPrvdr.setFeedType(feedType);
    }

    public void setFeedType(FeedPrvdr.FeedType feedType, @NonNull String id) {
        this.mFeedType = feedType;
        userOrTopicId = id;
        feedPrvdr.setFeedType(feedType);
    }

    public void setFeedType(FeedPrvdr.FeedType feedType) {
        this.mFeedType = feedType;
        feedPrvdr.setFeedType(feedType);
    }

    public interface FeedListListener {
        void onShowFeedDetail(FeedItem mFeedItem);
    }

    static class FeedDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, ScreenUtils.dp2px(3, parent.getContext()));
        }
    }
}
