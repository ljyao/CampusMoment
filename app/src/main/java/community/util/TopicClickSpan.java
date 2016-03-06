
package community.util;

import android.content.Context;
import android.view.View;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.ui.utils.textspan.AbsClickSpan;

import community.activity.TopicActivity_;

public class TopicClickSpan extends AbsClickSpan {

    Topic mTopic;
    Context mContext;

    public TopicClickSpan(Context context, Topic topic) {
        mTopic = topic;
        mContext = context;
    }

    @Override
    protected void doAfterLogin(View v) {
        TopicActivity_.intent(mContext).topic(mTopic).start();
    }
}
