package community.providable;

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

    public FeedPrvdr(FeedType feedType) {
        mFeedType = feedType;
        mCommunitySDK = App.getCommunitySDK();
    }

    public void getFirstPageData(NetLoaderListener<List<FeedItem>> listener) {
        switch (mFeedType) {
            case FollowFeed:
                getFollowFeedData(listener);
                break;
            case MeFeed:
                CommUser user = CommConfig.getConfig().loginedUser;
                getUserFeed(user, listener);
                break;
        }
    }

    private void getUserFeed(CommUser user, final NetLoaderListener<List<FeedItem>> listener) {
        mCommunitySDK.fetchUserTimeLine(user.id, new Listeners.FetchListener<FeedsResponse>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(FeedsResponse feedsResponse) {
                mNextPageUrl = feedsResponse.nextPageUrl;
                listener.onComplete(true, feedsResponse.result);
            }
        });

    }

    private void getFollowFeedData(final NetLoaderListener<List<FeedItem>> listener) {
        mCommunitySDK.fetchMyFollowedFeeds(new Listeners.FetchListener<FeedsResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(FeedsResponse feedsResponse) {
                mNextPageUrl = feedsResponse.nextPageUrl;
                listener.onComplete(true, feedsResponse.result);
            }
        });
    }

    public void fetchNextPageData(final NetLoaderListener<List<FeedItem>> listener) {
        if (TextUtils.isEmpty(mNextPageUrl)) {
            return;
        }
        mCommunitySDK.fetchNextPageData(mNextPageUrl,
                FeedsResponse.class, new Listeners.SimpleFetchListener<FeedsResponse>() {
                    @Override
                    public void onComplete(FeedsResponse response) {
                        // 根据response进行Toast
                        if (NetworkUtils.handleResponseAll(response)) {
                            if (response.errCode == ErrorCode.NO_ERROR) {
                                // 如果返回的数据是空，则需要置下一页地址为空
                                mNextPageUrl = "";
                                listener.onComplete(false, null);
                            }
                            return;
                        }
                        mNextPageUrl = response.nextPageUrl;
                        listener.onComplete(true, response.result);
                    }
                });
    }

    public enum FeedType {
        FollowFeed,
        TopicFeed,
        UserFeed,
        MeFeed,
        NearFeed
    }

}
