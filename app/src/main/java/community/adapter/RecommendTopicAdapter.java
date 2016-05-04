package community.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.ui.adapters.BackupAdapter;
import com.umeng.comm.ui.adapters.viewholders.ActiveUserViewHolder;

import community.activity.TopicDetailActivity_;


/**
 * 推荐话题的Adapter
 */
public class RecommendTopicAdapter extends BackupAdapter<Topic, ActiveUserViewHolder> {

    private static final String DIVIDER = " / ";
    private static String mFeedsStr = "";
    private static String mFansStr = "";
    private FollowListener<Topic> mListener;
    private boolean isFromFindPage = false;// 是否来自于发现页面。对于来自发现页面需要单独处理，

    private int mTopicColor = 0;
    private int mTopicIcon = 0;

    /**
     * 推荐话题的显示样式跟推荐用户的样式相同
     *
     * @param context
     */
    public RecommendTopicAdapter(Context context) {
        super(context);
        mTopicColor = ResFinder.getColor("umeng_comm_text_topic_light_color");
        mFeedsStr = ResFinder.getString("umeng_comm_feeds_num");
        mFansStr = ResFinder.getString("umeng_comm_fans_num");
        mTopicColor = ResFinder.getColor("umeng_comm_text_topic_light_color");
        mTopicIcon = ResFinder.getResourceId(ResType.DRAWABLE,
                "umeng_comm_topic_icon");
    }

    @Override
    protected ActiveUserViewHolder createViewHolder() {
        return new ActiveUserViewHolder();
    }

    @Override
    protected void setItemData(int position, final ActiveUserViewHolder viewHolder, View rootView) {
        final Topic topic = getItem(position);
        viewHolder.mUserNameTextView.setText(topic.name);
        viewHolder.mUserNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        viewHolder.mImageView.setImageResource(mTopicIcon);
        viewHolder.mImageView.setImageUrl(topic.icon, ImgDisplayOption.getTopicIconOption());
        viewHolder.mGenderImageView.setVisibility(View.GONE);
        viewHolder.mMsgFansTextView.setText(buildMsgFansStr(topic));

        setToggleButtonStatusAndEvent(viewHolder, topic);

        if (isFromFindPage) {
            viewHolder.mUserNameTextView.setTextColor(mTopicColor);
            rootView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    gotoTopicDetailPage(topic);
                }
            });
        }

    }

    /**
     * 构建feed数量、粉丝数量的字符串
     *
     * @param topic
     * @return
     */
    protected String buildMsgFansStr(Topic topic) {
        StringBuilder builder = new StringBuilder(mFeedsStr);
        builder.append(topic.feedCount);
        builder.append(DIVIDER).append(mFansStr);
        builder.append(topic.fansCount);
        return builder.toString();
    }

    /**
     * 跳转到话题详情页面</br>
     *
     * @param topic
     */
    private void gotoTopicDetailPage(Topic topic) {
        TopicDetailActivity_.intent(mContext).topic(topic).start();
    }

    public void setFollowListener(FollowListener<Topic> listener) {
        this.mListener = listener;
    }

    /**
     * 设置是否来自于发送页面</br>
     *
     * @param fromFind
     */
    public void setFromFindPage(boolean fromFind) {
        isFromFindPage = fromFind;
    }

    private void setToggleButtonStatusAndEvent(final ActiveUserViewHolder viewHolder,
                                               final Topic topic) {
        viewHolder.mToggleButton.setChecked(topic.isFocused);
        viewHolder.mToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.onFollowOrUnFollow(topic, viewHolder.mToggleButton,
                        viewHolder.mToggleButton.isChecked());
            }
        });
    }

    public interface FollowListener<T> {
        void onFollowOrUnFollow(T t, ToggleButton toggleButton, boolean isFollow);
    }

}
