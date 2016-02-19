/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package community.providable;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.TopicItemResponse;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.uy.App;

/**
 *
 */
public class TopicDetailPrvdr {


    private CommunitySDK mCommunitySDK;

    public TopicDetailPrvdr() {
        mCommunitySDK = App.getCommunitySDK();
    }

    public void fetchTopicWithId(String id, final NetLoaderListener<Topic> listener) {
        mCommunitySDK.fetchTopicWithId(id, new Listeners.FetchListener<TopicItemResponse>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(TopicItemResponse topicItemResponse) {
                listener.onComplete(true, topicItemResponse.result);
            }
        });
    }

    /**
     * 关注某个话题</br>
     *
     * @param topic 话题的id
     */
    public void followTopic(final Topic topic, final NetLoaderListener<Boolean> listener) {
        // 关注话题
        mCommunitySDK.followTopic(topic,
                new SimpleFetchListener<Response>() {

                    @Override
                    public void onComplete(Response response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_follow_failed");
                            listener.onComplete(true, false);
                            return;
                        }
                        String resName = "";
                        if (response.errCode == ErrorCode.NO_ERROR) {
                            resName = "umeng_comm_topic_follow_success";
                            listener.onComplete(true, true);
                            topic.isFocused = true;
                            CommUser user = CommConfig.getConfig().loginedUser;
                            DatabaseAPI.getInstance().getTopicDBAPI()
                                    .saveFollowedTopicToDB(user.id, topic);

                        } else if (response.errCode == ErrorCode.ORIGIN_TOPIC_DELETE_ERR_CODE) {
                            // 在数据库中删除该话题并Toast
                            resName = "umeng_comm_topic_has_deleted";
                            DatabaseAPI.getInstance().getTopicDBAPI()
                                    .deleteTopicDataFromDB(topic.id);
                        } else if (response.errCode == ErrorCode.ERROR_TOPIC_FOCUSED) {
                            resName = "umeng_comm_topic_has_focused";
                            listener.onComplete(true, true);
                        } else {
                            resName = "umeng_comm_topic_follow_failed";
                            listener.onComplete(true, true);
                        }
                        ToastMsg.showShortMsgByResName(resName);
                    }
                });
    }

    /**
     * 取消关注某个话题</br>
     *
     * @param topic
     */
    public void cancelFollowTopic(final Topic topic, final NetLoaderListener<Boolean> listener) {
        // 取消关注话题
        mCommunitySDK.cancelFollowTopic(topic,
                new SimpleFetchListener<Response>() {

                    @Override
                    public void onComplete(Response response) {
                        if (NetworkUtils.handleResponseComm(response)) {
                            ToastMsg.showShortMsgByResName("umeng_comm_topic_cancel_failed");
                            listener.onComplete(true, true);
                            return;
                        }
                        String resName = "";
                        if (response.errCode == ErrorCode.NO_ERROR) {
                            resName = "umeng_comm_topic_cancel_success";
                            topic.isFocused = false;
                            listener.onComplete(true, false);
                            DatabaseAPI.getInstance().getTopicDBAPI().deleteTopicFromDB(topic.id);

                        } else if (response.errCode == ErrorCode.ORIGIN_TOPIC_DELETE_ERR_CODE) {
                            ToastMsg.showShortMsgByResName("umeng_comm__topic_has_deleted");
                            DatabaseAPI.getInstance().getTopicDBAPI()
                                    .deleteTopicDataFromDB(topic.id);
                        } else if (response.errCode == ErrorCode.ERROR_TOPIC_NOT_FOCUSED) {
                            resName = "umeng_comm_topic_has_not_focused";
                            listener.onComplete(true, false);
                        } else {
                            resName =
                                    "umeng_comm_topic_cancel_failed";
                            listener.onComplete(true, false);

                        }
                        ToastMsg.showShortMsgByResName(resName);
                    }
                });
    }

}
