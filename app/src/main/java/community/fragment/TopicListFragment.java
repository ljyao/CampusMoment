package community.fragment;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Topic;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.List;

import community.adapter.TopicListAdapter;
import community.providable.NetLoaderListener;
import community.providable.TopicPrvdr;
import fragment.RefreshRecycleFragment;
import helper.common_util.ScreenUtils;

/**
 * 用户已经关注的话题Fragment
 */
@EFragment
public class TopicListFragment extends RefreshRecycleFragment<TopicListAdapter> {
    @FragmentArg
    public CommUser user;
    public TopicPrvdr prvdr;

    @AfterViews
    public void initView() {
        prvdr = new TopicPrvdr();
        adapter = new TopicListAdapter();
        listView.setAdapter(adapter);
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemSpace = ScreenUtils.dp2px(1, view.getContext());
                outRect.set(0, itemSpace, 0, itemSpace);
                int index = parent.indexOfChild(view);
                if (index == 0) {
                    outRect.set(0, 0, 0, itemSpace);
                }
            }
        });
        loadData();
    }

    private void loadData() {
        refreshLayout.setRefreshing(true);
        prvdr.loadFollowedTopics(user.id, new NetLoaderListener<List<Topic>>() {
            @Override
            public void onComplete(boolean statue, List<Topic> result) {
                refreshLayout.setRefreshing(false);
                if (statue)
                    adapter.update(result);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected void onRefreshing() {
        loadData();
    }

    @Override
    protected void onLoadMore() {

    }
}
