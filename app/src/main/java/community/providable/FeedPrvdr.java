package community.providable;

import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.util.List;

/**
 * Created by ljy on 15/12/28.
 */
public class FeedPrvdr {
    private FeedType mFeedType;
    private String mNextPageUrl;
    private CommunitySDK mCommunitySDK;
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

    public FeedPrvdr(FeedType feedType) {
        mFeedType = feedType;
        mCommunitySDK = App.getCommunitySDK();
    }

    public void getFirstPageData(NetLoaderListener<List<FeedItem>> listener, @Nullable String id, @Nullable Location location) {
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
                loadFeedByTopic(id);
                break;
            case UserFeed:
                getUserFeed(id);
                break;
            case LocationFeed:
                getLocationFeed(location);
                break;
        }
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

    public enum FeedType {
        FollowFeed,
        TopicFeed,
        UserFeed,
        MeFeed,
        NearFeed,
        LocationFeed
    }

}
