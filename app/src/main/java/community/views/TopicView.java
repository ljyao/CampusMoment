package community.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.ui.widgets.RoundImageView;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import adapter.ViewWrapper;
import community.activity.TopicDetailActivity_;

/**
 * Created by Shine on 2016/5/4.
 */
@EViewGroup(R.layout.view_topic)
public class TopicView extends RelativeLayout implements ViewWrapper.Binder<Topic> {
    @ViewById(R.id.topic_name)
    public TextView topicNameTv;
    @ViewById(R.id.topic_icon)
    public RoundImageView topicIconIv;
    @ViewById(R.id.topic_msg)
    public TextView topicIntroductionTv;
    private Topic topic;

    public TopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    public void initViews() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicDetailActivity_.intent(getContext()).topic(topic).start();
            }
        });
    }

    @Override
    public void bind(Topic topic) {
        this.topic = topic;
        topicNameTv.setText(topic.name);
        topicIconIv.setImageResource(R.drawable.topic_icon);
        topicIconIv.setImageUrl(topic.icon, ImgDisplayOption.getTopicIconOption());
        topicIntroductionTv.setText(buildTopicMsg(topic));

    }

    protected String buildTopicMsg(Topic topic) {
        if (TextUtils.isEmpty(topic.desc) || topic.desc.equals("null")) {
            topic.desc = "";
        }
        return topic.desc;
    }
}
