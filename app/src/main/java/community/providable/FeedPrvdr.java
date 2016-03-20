package community.providable;

import android.location.Location;
import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedCommentResponse;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljy on 15/12/28.
 */
public class FeedPrvdr {
    private FeedType mFeedType;
    private String mNextPageUrl;
    private CommunitySDK mCommunitySDK;
    private String topicId;
    private String userId;
    private Location location;
    private NetLoaderListener<List<FeedItem>> feedFragmentListener;
    Listeners.FetchListener<FeedsResponse> fetchListener = new Listeners.FetchListener<FeedsResponse>() {
        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(FeedsResponse response) {
            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response)) {
                if (response.errCode == ErrorCode.NO_ERROR) {
                    // 如果返回的数据是空，则需要置下一页地址为空
                    mNextPageUrl = "";
                    feedFragmentListener.onComplete(false, null);
                }
                return;
            }
            mNextPageUrl = response.nextPageUrl;
            feedFragmentListener.onComplete(true, response.result);
        }
    };
    protected Listeners.SimpleFetchListener<FeedCommentResponse> mCommentListener = new Listeners.SimpleFetchListener<FeedCommentResponse>() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(FeedCommentResponse response) {
            if (NetworkUtils.handleResponseAll(response)) {
                return;
            }
            mNextPageUrl = response.nextPageUrl;
            feedFragmentListener.onComplete(true, response.result);
        }
    };


    public FeedPrvdr(FeedType feedType) {
        mFeedType = feedType;
        mCommunitySDK = App.getCommunitySDK();
    }

    public void getFirstPageData(NetLoaderListener<List<FeedItem>> listener) {
        this.feedFragmentListener = listener;
        switch (mFeedType) {
            case FollowFeed:
                getFollowFeedData();
                break;
            case MeFeed:
                CommUser user = CommConfig.getConfig().loginedUser;
                getUserFeed(user.id);
                break;
            case TopicFeed:
                loadFeedByTopic(topicId);
                break;
            case UserFeed:
                getUserFeed(userId);
                break;
            case LocationFeed:
                getLocationFeed(location);
                break;
            case ReceivedComments:
                getReceivedComments();
                break;
        }
    }

    private void getReceivedComments() {
        mCommunitySDK.fetchReceivedComments(0, mCommentListener);
    }

    private void getLocationFeed(Location mLocation) {
        mCommunitySDK.fetchNearByFeed(mLocation, fetchListener);
    }

    private void getUserFeed(String id) {
        mCommunitySDK.fetchUserTimeLine(id, fetchListener);

    }

    private void getFollowFeedData() {
        mCommunitySDK.fetchMyFollowedFeeds(fetchListener);
    }

    public void fetchNextPageData(NetLoaderListener<List<FeedItem>> listener) {
        if (TextUtils.isEmpty(mNextPageUrl)) {
            return;
        }
        feedFragmentListener = listener;
        mCommunitySDK.fetchNextPageData(mNextPageUrl, FeedsResponse.class, fetchListener);
    }

    public void loadFeedByTopic(String id) {
        mCommunitySDK.fetchTopicFeed(id, fetchListener);
    }


    public void setFeedType(FeedType feedType) {
        this.mFeedType = feedType;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public enum FeedType implements Serializable {
        FollowFeed,
        TopicFeed,
        UserFeed,
        MeFeed,
        NearFeed,
        LocationFeed,
        ReceivedComments
    }

}
