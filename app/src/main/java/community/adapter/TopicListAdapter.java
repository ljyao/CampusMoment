package community.adapter;


import android.view.ViewGroup;

import com.umeng.comm.core.beans.Topic;

import adapter.RecycleAdapter;
import community.views.TopicView;
import community.views.TopicView_;

/**
 * Created by Shine on 2016/5/4.
 */
public class TopicListAdapter extends RecycleAdapter<Topic, TopicView> {

    @Override
    protected TopicView onCreateItemView(ViewGroup parent, int viewType) {
        return TopicView_.build(parent.getContext(), null);
    }
}
