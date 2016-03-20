package community.fragment;

import android.os.Bundle;
import android.view.View;

import com.umeng.comm.ui.adapters.CommentMeAdapter;
import com.umeng.comm.ui.adapters.FeedAdapter;
import com.umeng.comm.ui.fragments.FeedListFragment;
import com.umeng.comm.ui.presenter.impl.CommentReceivedPresenter;

/**
 * 我的消息中接到的评论Fragment
 */
public class CommentReceivedFragment extends FeedListFragment<CommentReceivedPresenter> {

    @Override
    protected CommentReceivedPresenter createPresenters() {
        return new CommentReceivedPresenter(this);
    }

    @Override
    protected FeedAdapter createListViewAdapter() {
        return new CommentMeAdapter(getActivity(), true);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        mPostBtn.setVisibility(View.GONE);
    }

    @Override
    protected void initRefreshView() {
        super.initRefreshView();
        mFeedsListView.setOnItemClickListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}
