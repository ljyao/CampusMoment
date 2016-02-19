package community.views;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.Topic;
import com.uy.bbs.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by shine on 16-2-18.
 */
@EViewGroup(R.layout.topic_info)
public class TopicInfoView extends RelativeLayout {
    @ViewById(R.id.topic_icon)
    public SimpleDraweeView topicIcon;
    @ViewById(R.id.feeds_num)
    public TextView feedsNum;
    @ViewById(R.id.fans_num)
    public TextView fansNum;
    @ViewById(R.id.photos_num)
    public TextView photosNum;
    @ViewById(R.id.follow_btn)
    public TextView followBtn;
    @ViewById(R.id.topic_introduce)
    public TextView topicIntroduce;

    public TopicInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(Topic topic) {
        if (!TextUtils.isEmpty(topic.icon) && !"null".equals(topic.icon)) {
            topicIcon.setImageURI(Uri.parse(topic.icon));
        } else {
            topicIcon.setBackgroundResource(R.drawable.topic_icon);
        }
        feedsNum.setText(topic.feedCount + "");
        fansNum.setText(topic.fansCount + "");
        photosNum.setText(topic.imageItems.size() + "");
        if (topic.isFocused) {
            followBtn.setText("已关注");
            followBtn.setBackgroundColor(getResources().getColor(R.color.followed_bg));
        } else {
            followBtn.setText("关注");
            followBtn.setBackgroundColor(getResources().getColor(R.color.primary_light));
        }
        if (TextUtils.isEmpty(topic.desc)) {
            topicIntroduce.setVisibility(GONE);
        } else {
            topicIntroduce.setText(topic.desc);
        }
    }
}
