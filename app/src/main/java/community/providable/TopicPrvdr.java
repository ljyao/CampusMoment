package community.providable;

import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
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
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    //  如果是网络错误，其结果可能快于DB查询
                    if (CommonUtils.isNetworkErr(response.errCode)) {
                    }
                    return;
                }
                listener.onComplete(true, response.result);
                mNextPageUrl = response.result.get(0).nextPage;
            }
        });
    }

    public void loadMoreData() {
        if (TextUtils.isEmpty(mNextPageUrl)) {
            return;
        }
        mCommunitySDK.fetchNextPageData(mNextPageUrl, TopicResponse.class, new Listeners.FetchListener<TopicResponse>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(TopicResponse response) {
                // 根据response进行Toast
                if (NetworkUtils.handleResponseAll(response)) {
                    return;
                }
                final List<Topic> results = response.result;
            }
        });
    }
}
