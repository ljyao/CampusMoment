package community.providable;

import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FansResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.util.List;

/**
 * Created by Shine on 2016/3/21.
 */
public class FollowedsAndFansPrvdr {
    private final CommunitySDK mCommunitySDK;
    private String nextPageUrl;
    private String mUid;
    private NetLoaderListener<List<CommUser>> loaderListener;
    Listeners.FetchListener fetchListener = new Listeners.FetchListener<FansResponse>() {
        @Override
        public void onStart() {
        }

        @Override
        public void onComplete(FansResponse response) {
            // 根据response进行Toast
            if (NetworkUtils.handleResponseAll(response)) {
                if (response.errCode == ErrorCode.NO_ERROR) {
                    nextPageUrl = "";
                }
                loaderListener.onComplete(false, null);
                return;
            }
            final List<CommUser> users = response.result;
            loaderListener.onComplete(true, users);
            nextPageUrl = response.nextPageUrl;
        }
    };

    public FollowedsAndFansPrvdr(String id, NetLoaderListener<List<CommUser>> listener) {
        mUid = id;
        mCommunitySDK = App.getCommunitySDK();
        loaderListener = listener;
    }

    public void loadMoreData() {
        if (TextUtils.isEmpty(nextPageUrl)) {
            return;
        }
        mCommunitySDK.fetchNextPageData(nextPageUrl, FansResponse.class, fetchListener);
    }

    public void getFolloweds() {
        mCommunitySDK.fetchFollowedUser(mUid, fetchListener);
    }

    public void getFans() {
        mCommunitySDK.fetchFans(mUid, fetchListener);
    }

}
