package community.adapter;

import android.view.ViewGroup;

import com.umeng.comm.core.beans.FeedItem;

import adapter.RecycleAdapter;
import community.fragment.FeedFragment;
import community.views.FollowedFeedView;
import community.views.FollowedFeedView_;

/**
 * Created by ljy on 15/12/21.
 */
public class FeedAdapter extends RecycleAdapter<FeedItem, FollowedFeedView> {
    private FeedFragment.FeedListListener feedListListener;

    @Override
    protected FollowedFeedView onCreateItemView(ViewGroup parent, int viewType) {
        FollowedFeedView feedView = FollowedFeedView_.build(parent.getContext(), null);
        feedView.setListener(feedListListener);
        return feedView;
    }

    public void setListener(FeedFragment.FeedListListener feedListListener) {
        this.feedListListener = feedListListener;
    }
}
