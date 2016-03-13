
package community.providable;

import android.text.TextUtils;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.responses.FansResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.uy.App;

import java.util.List;


/**
 *
 */
public class FollowedUserFgPresenter {

    protected String mUid;
    protected String nextPageUrl;
    private boolean hasRefresh = false;

    public FollowedUserFgPresenter(String uid) {
        this.mUid = uid;
    }


    public void followedUser() {
        App.getCommunitySDK().fetchFollowedUser(mUid, new FetchListener<FansResponse>() {
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
                    return;
                }

                final List<CommUser> followedUsers = response.result;

                // 解析下一页地址
                parseNextpageUrl(response, true);

            }
        });
    }


    public void loadMoreData() {
        if (TextUtils.isEmpty(nextPageUrl)) {
            return;
        }

        App.getCommunitySDK().fetchNextPageData(nextPageUrl, FansResponse.class,
                new SimpleFetchListener<FansResponse>() {

                    @Override
                    public void onComplete(FansResponse response) {
                        // 根据response进行Toast
                        if (NetworkUtils.handleResponseAll(response)) {
                            if (response.errCode == ErrorCode.NO_ERROR) {
                                nextPageUrl = "";
                            }
                            return;
                        }
                        parseNextpageUrl(response, false);

                    }
                });
    }


    protected void parseNextpageUrl(FansResponse response, boolean fromRefersh) {
        if (fromRefersh && TextUtils.isEmpty(nextPageUrl) && !hasRefresh) {
            hasRefresh = true;
            nextPageUrl = response.nextPageUrl;
        } else if (!fromRefersh) {
            nextPageUrl = response.nextPageUrl;
        }
    }
}
