package community.fragment;

import android.view.View;

import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.presenter.impl.FollowedTopicPresenter;
import com.umeng.comm.ui.presenter.impl.RecommendTopicPresenter;

/**
 * 用户已经关注的话题Fragment
 */
public class FollowedTopicFragment extends TopicFragment {

    public String mUid;

    @Override
    protected RecommendTopicPresenter createPresenters() {
        mUid = getArguments().getString("uid");
        return new FollowedTopicPresenter(mUid, this);
    }

    @Override
    protected void initSearchView(View rootView) {
        super.initSearchView(rootView);
        mSearchLayout.setVisibility(View.GONE);
    }

    protected void initRefreshView(View rootView) {
        super.initRefreshView(rootView);
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_focus_topic"));
    }
}
