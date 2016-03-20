package community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;

import java.util.List;

import community.fragment.FeedFragment;
import community.providable.FeedPrvdr;
import community.views.FeedItemView;
import community.views.FeedItemView_;
import community.views.TopicInfoView;
import community.views.TopicInfoView_;

/**
 * Created by ljy on 15/12/21.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private static final int FEED = 0;
    private static final int HEADVIEW = 1;
    private FeedFragment.FeedListListener feedListListener;
    private Topic topic;
    private FeedPrvdr.FeedType feedType;
    private List<FeedItem> items;

    public FeedAdapter(FeedPrvdr.FeedType mFeedType) {
        feedType = mFeedType;
    }


    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case HEADVIEW:
                itemView = TopicInfoView_.build(parent.getContext(), null);
                break;
            case FEED:
                FeedItemView feedView = FeedItemView_.build(parent.getContext(), null);
                if (feedType == FeedPrvdr.FeedType.ReceivedComments) {
                    feedView.isReceivedComment = true;
                }
                feedView.setListener(feedListListener);
                itemView = feedView;
                break;
        }

        return new FeedViewHolder(itemView, feedType);
    }

    public void setListener(FeedFragment.FeedListListener feedListListener) {
        this.feedListListener = feedListListener;
    }

    @Override
    public void onBindViewHolder(FeedViewHolder viewHolder, int position) {
        if (feedType == FeedPrvdr.FeedType.TopicFeed) {
            if (position == 0) {
                viewHolder.setData(topic);
            } else {
                viewHolder.setData(items.get(position - 1));
            }
        } else {
            viewHolder.setData(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (items == null || items.size() == 0) {
            if (feedType == FeedPrvdr.FeedType.TopicFeed) {
                return 1;
            } else {
                return 0;
            }
        }
        return feedType == FeedPrvdr.FeedType.TopicFeed ? items.size() + 1 : items.size();
    }

    public void update(List<FeedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void append(List<FeedItem> items) {
        append(this.items.size(), items);
    }

    public void append(int position, List<FeedItem> items) {
        this.items.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (feedType != null && feedType == FeedPrvdr.FeedType.TopicFeed && position == 0) {
            return HEADVIEW;
        } else {
            return FEED;
        }
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    static public class FeedViewHolder extends RecyclerView.ViewHolder {
        private FeedPrvdr.FeedType feedType;
        private FeedItemView feedView;
        private TopicInfoView topicInfo;

        public FeedViewHolder(View itemView, FeedPrvdr.FeedType feedType) {
            super(itemView);
            this.feedType = feedType;
            if (itemView instanceof FeedItemView) {
                feedView = (FeedItemView) itemView;
            } else if (itemView instanceof TopicInfoView) {
                topicInfo = (TopicInfoView) itemView;
            }
        }

        public void setData(FeedItem feedItem) {
            feedView.bind(feedItem);
            if (feedType == FeedPrvdr.FeedType.ReceivedComments) {
                feedView.hideActionButtons();
            }
        }

        public void setData(Topic topic) {
            topicInfo.bind(topic);
        }
    }
}
