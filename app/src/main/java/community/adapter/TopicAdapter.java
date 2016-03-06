package community.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.umeng.comm.core.beans.Topic;

/**
 * 首页的话题Adapter
 */
public class TopicAdapter extends RecommendTopicAdapter {

    public TopicAdapter(Context context) {
        super(context);
        setFromFindPage(true);
    }

    @Override
    protected String buildMsgFansStr(Topic topic) {
        if (TextUtils.isEmpty(topic.desc) || topic.desc.equals("null")) {
            topic.desc = "";
        }
        return topic.desc;
    }
}
