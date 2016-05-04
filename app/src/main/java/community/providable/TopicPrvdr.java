package community.providable;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.util.List;

/**
 * Created by Shine on 2016/2/12.
 */
public class TopicPrvdr {
    private CommunitySDK mCommunitySDK;
    private String mNextPageUrl;

    public TopicPrvdr() {
        mCommunitySDK = App.getCommunitySDK();
    }

    public void loadTopicList(final NetLoaderListener<List<Topic>> listener) {

        mCommunitySDK.fetchTopics(new Listeners.FetchListener<TopicResponse>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(final TopicResponse response) {
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                listener.onComplete(true, response.result);
                mNextPageUrl = response.result.get(0).nextPage;
            }
        });
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
