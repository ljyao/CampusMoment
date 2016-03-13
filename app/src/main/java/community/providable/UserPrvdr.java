package community.providable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.ImageResponse;
import com.umeng.comm.core.nets.responses.PortraitUploadResponse;
import com.umeng.comm.core.nets.responses.ProfileResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ToastMsg;
import com.uy.App;
import com.uy.util.Worker;

/**
 * Created by ljy on 15/12/24.
 */
public class UserPrvdr {
    private CommunitySDK mCommunitySDK = App.getCommunitySDK();

    public void getUserInfo(final CommUser user, final NetLoaderListener<CommUser> userListener) {
        mCommunitySDK.fetchUserProfile(user.id, new Listeners.FetchListener<ProfileResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(ProfileResponse profileResponse) {
                user.fansCount = profileResponse.mFansCount;
                user.feedCount = profileResponse.mFeedsCount;
                user.followCount = profileResponse.mFollowedUserCount;
                user.isFollowed = profileResponse.hasFollowed;
                userListener.onComplete(true, user);
            }
        });
    }

    public void updateUserProfile(final CommUser user, final UserListener listener) {
        mCommunitySDK.updateUserProfile(user, new Listeners.CommListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(Response response) {
                if (response.errCode == ErrorCode.NO_ERROR) {
                    CommConfig.getConfig().loginedUser = user;
                    listener.onComplete(user);
                }
            }
        });
    }

    public void updateUserProtrait(final String path, final UserListener listener) {
        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                ImageResponse imageResponse = mCommunitySDK.uploadImage("file://" + path);
                CommUser user = CommConfig.getConfig().loginedUser;
                user.iconUrl = imageResponse.result.originImageUrl;
                updateUserProfile(user, listener);
            }
        });

    }

    public void updateUserProtrait(Bitmap bmp, final UserListener listener, final Context context) {
        mCommunitySDK.updateUserProtrait(bmp, new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
            @Override
            public void onComplete(PortraitUploadResponse response) {
                if (response != null && response.errCode == ErrorCode.NO_ERROR) {
                    Log.d("", "头像更新成功 : " + response.mJsonObject.toString());
                    CommUser user = CommConfig.getConfig().loginedUser;
                    user.iconUrl = response.mIconUrl;
                    Log.d("", "#### 登录用户的头像 : " + CommConfig.getConfig().loginedUser.iconUrl);
                    CommonUtils.saveLoginUserInfo(context, user);
                    listener.onComplete(user);
                }
            }
        });
    }

    public void updateUserProtrait(final String path, final UserListener listener, final Context context) {
        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                final Bitmap bmp = BitmapFactory.decodeFile(path);
                mCommunitySDK.updateUserProtrait(bmp, new Listeners.SimpleFetchListener<PortraitUploadResponse>() {
                    @Override
                    public void onComplete(PortraitUploadResponse response) {
                        if (response != null && response.errCode == ErrorCode.NO_ERROR) {
                            Log.d("", "头像更新成功 : " + response.mJsonObject.toString());
                            final CommUser user = CommConfig.getConfig().loginedUser;
                            user.iconUrl = response.mIconUrl;
                            Log.d("", "#### 登录用户的头像 : " + CommConfig.getConfig().loginedUser.iconUrl);
                            CommonUtils.saveLoginUserInfo(context, user);
                            bmp.recycle();
                            listener.onComplete(user);
                        }
                    }
                });
            }
        });
    }

    /**
     * 关注某个好友</br>
     *
     * @param user
     */
    public void followUser(final CommUser user, final NetLoaderListener<Boolean> listener) {
        mCommunitySDK.followUser(user, new Listeners.SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if (NetworkUtils.handleResponseComm(response)) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_failed");
                    user.isFollowed = false;
                    listener.onComplete(true, user.isFollowed);
                    return;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_user_success");
                    user.isFollowed = true;
                    DatabaseAPI.getInstance().getFollowDBAPI().follow(user);
                    listener.onComplete(true, user.isFollowed);
                    return;
                }
                if (response.errCode == ErrorCode.ERROR_USER_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_focused");
                    user.isFollowed = true;
                    listener.onComplete(true, user.isFollowed);
                    return;
                }
                user.isFollowed = false;
                ToastMsg.showShortMsgByResName("umeng_comm_follow_user_failed");
                listener.onComplete(true, user.isFollowed);
            }
        });
    }

    /**
     * 取消关注某个好友</br>
     *
     * @param user
     */
    public void cancelFollowUser(final CommUser user, final NetLoaderListener<Boolean> listener) {

        mCommunitySDK.cancelFollowUser(user, new Listeners.SimpleFetchListener<Response>() {

            @Override
            public void onComplete(Response response) {
                if (NetworkUtils.handleResponseComm(response)) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_failed");
                    user.isFollowed = true;
                    listener.onComplete(true, user.isFollowed);
                    return;
                }
                if (response.errCode == ErrorCode.NO_ERROR) {
                    ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_success");
                    user.isFollowed = false;
                    listener.onComplete(true, user.isFollowed);
                    return;
                }
                if (response.errCode == ErrorCode.ERROR_USER_NOT_FOCUSED) {
                    ToastMsg.showShortMsgByResName("umeng_comm_user_has_not_focused");
                    user.isFollowed = false;
                    listener.onComplete(true, user.isFollowed);
                    return;
                }
                ToastMsg.showShortMsgByResName("umeng_comm_follow_cancel_failed");
                user.isFollowed = true;
                listener.onComplete(true, user.isFollowed);
            }
        });
    }

    public interface UserListener {
        void onComplete(CommUser user);
    }

}
