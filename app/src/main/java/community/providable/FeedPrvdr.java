package community.providable;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedCommentResponse;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.LikeMeResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.comm.core.utils.ToastMsg;
import com.uy.App;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.DatabaseHelper;

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
    private Dao<model.FeedItem, String> feedDao;
    private boolean isFirstLoad = true;
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
                }
                feedFragmentListener.onComplete(false, null);
                return;
            }
            mNextPageUrl = response.nextPageUrl;
            updateLocalData(response.result, isFirstLoad);
            isFirstLoad = false;
            feedFragmentListener.onComplete(true, response.result);
        }
    };

    public FeedPrvdr(FeedType feedType) {
        mFeedType = feedType;
        mCommunitySDK = App.getCommunitySDK();
    }

    private List<FeedItem> removeEmptyData(List<FeedItem> result) {
        Iterator<FeedItem> iterator = result.iterator();
        while (iterator.hasNext()) {
            FeedItem item = iterator.next();
            if (item.sourceFeed == null) {
                iterator.remove();
            } else {
                item.text = "赞了这条消息";
            }
        }
        return result;
    }

    public List<FeedItem> getLocalData() {
        List<FeedItem> feeds = new ArrayList<>();
        feedDao = DatabaseHelper.getHelper(App.getApp()).getFeedDao();
        try {
            List<model.FeedItem> localFeeds = feedDao.queryForEq("feedType", mFeedType.ordinal());
            for (model.FeedItem localFeed : localFeeds) {
                feeds.add(model.FeedItem.toValueOf(localFeed));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feeds;
    }
    public void getFirstPageData(NetLoaderListener<List<FeedItem>> listener) {
        isFirstLoad = true;
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
            case NearFeed:
                getLocationFeed(App.getApp());
                break;
            case ReceivedComments:
                getReceivedComments();
                break;
            case HotestFeed:
                getHotestFeeds();
            case RealTimeFeed:
                getRealTimeFeed();
            case AtFeeds:
                getAtFeeds();
                break;
            case LikedMe:
                getLikeMe();
                break;
        }
    }

    public void updateLocalData(List<FeedItem> feeds, boolean isFirstLoad) {
        if (feeds == null || feeds.size() == 0) {
            return;
        }
        try {
            feedDao = DatabaseHelper.getHelper(App.getApp()).getFeedDao();
            if (isFirstLoad) {
                DeleteBuilder<model.FeedItem, String> deleteBuilder = feedDao.deleteBuilder();
                deleteBuilder.where().eq("feedType", mFeedType.ordinal());
                deleteBuilder.delete();
            }
            for (FeedItem feedItme : feeds) {
                feedDao.create(model.FeedItem.valueOf(feedItme, mFeedType.ordinal()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getLikeMe() {
        mCommunitySDK.fetchLikedRecords(CommConfig.getConfig().loginedUser.id,
                new Listeners.SimpleFetchListener<LikeMeResponse>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(LikeMeResponse response) {
                        if (NetworkUtils.handleResponseAll(response)) {
                            feedFragmentListener.onComplete(false, null);
                            return;
                        }
                        mNextPageUrl = response.nextPageUrl;
                        feedFragmentListener.onComplete(true, removeEmptyData(response.result));
                    }
                });
    }

    private void getAtFeeds() {
        mCommunitySDK.fetchBeAtFeeds(0, fetchListener);
    }

    private void getRealTimeFeed() {
        mCommunitySDK.fetchRealTimeFeed(fetchListener);
    }

    private void getHotestFeeds() {
        mCommunitySDK.fetchHotestFeeds(fetchListener, 1, 0);
    }

    private void getReceivedComments() {
        mCommunitySDK.fetchReceivedComments(0, mCommentListener);
    }

    private void getLocationFeed(Context mContext) {
        LocationSDKManager.getInstance().getCurrentSDK()
                .requestLocation(mContext, new Listeners.SimpleFetchListener<Location>() {

                    @Override
                    public void onComplete(Location location) {
                        if (location == null) {
                            ToastMsg.showShortMsgByResName("umeng_comm_request_location_failed");
                            return;
                        }
                        mCommunitySDK.fetchNearByFeed(location, fetchListener);
                    }
                });
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
        HotestFeed,
        RealTimeFeed,
        ReceivedComments,
        AtFeeds,
        LikedMe
    }

}
