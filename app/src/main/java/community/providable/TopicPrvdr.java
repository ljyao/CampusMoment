package community.providable;

import com.j256.ormlite.dao.Dao;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.DatabaseHelper;

/**
 * Created by Shine on 2016/2/12.
 */
public class TopicPrvdr {
    private CommunitySDK mCommunitySDK;
    private String mNextPageUrl;
    private Dao<model.Topic, String> topicDao;

    public TopicPrvdr() {
        mCommunitySDK = App.getCommunitySDK();
    }

    public List<Topic> loadTopicList(final NetLoaderListener<List<Topic>> listener) {
        mCommunitySDK.fetchTopics(new Listeners.FetchListener<TopicResponse>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(final TopicResponse response) {
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                updateLocalData(response.result);
                listener.onComplete(true, response.result);
                mNextPageUrl = response.result.get(0).nextPage;
            }
        });
        topicDao = DatabaseHelper.getHelper(App.getApp()).getTopicDao();
        List<Topic> topics = new ArrayList<>();
        try {
            List<model.Topic> localTopics = topicDao.queryForAll();
            for (model.Topic topic : localTopics) {
                topics.add(model.Topic.toValue(topic));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    public void updateLocalData(List<Topic> topics) {
        if (topics == null || topics.size() == 0) {
            return;
        }
        try {
            topicDao = DatabaseHelper.getHelper(App.getApp()).getTopicDao();
            topicDao.deleteBuilder().delete();
            for (Topic topic : topics) {
                topicDao.create(model.Topic.valueOf(topic));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadFollowedTopics(String mUid, final NetLoaderListener<List<Topic>> listener) {
        mCommunitySDK.fetchFollowedTopics(mUid,
                new Listeners.SimpleFetchListener<TopicResponse>() {

                    @Override
                    public void onComplete(final TopicResponse response) {
                        if (NetworkUtils.handleResponseAll(response)) {
                            listener.onComplete(false, null);
                            return;
                        }
                        listener.onComplete(true, response.result);
                        mNextPageUrl = response.result.get(0).nextPage;
                    }

                });
    }

    public void loadRecommendedTopics(final NetLoaderListener<List<Topic>> listener) {

        mCommunitySDK.fetchRecommendedTopics(new Listeners.FetchListener<TopicResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(TopicResponse topicResponse) {

            }
        });
    }

}
