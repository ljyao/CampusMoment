
package community.fragment;

import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.List;

import community.adapter.UserAdapter;
import community.providable.FollowedsAndFansPrvdr;
import community.providable.NetLoaderListener;
import fragment.RefreshRecycleFragment;
import helper.common_util.ScreenUtils;


/**
 * 已关注的用户的Fragment
 */
@EFragment
public class FollowedUserFragment extends RefreshRecycleFragment<UserAdapter> {

    /**
     * 用户id。根据该uid获取该用户关注的好友信息
     */
    @FragmentArg
    public String mUserId;
    @FragmentArg
    public UserListType type;
    /**
     * 已关注好友的适配器
     */
    protected UserAdapter mAdapter;
    FollowedsAndFansPrvdr userPrvr;
    private boolean isfirstPage = true;
    private LinearLayoutManager layoutManager;

    private NetLoaderListener<List<CommUser>> listener = new NetLoaderListener<List<CommUser>>() {
        @Override
        public void onComplete(boolean statue, List<CommUser> result) {
            setRefreshState(false);
            if (!statue)
                return;
            if (isfirstPage) {
                adapter.update(result);
            } else {
                adapter.append(result);
            }
        }
    };

    @AfterViews
    public void initViews() {
        adapter = new UserAdapter();
        userPrvr = new FollowedsAndFansPrvdr(mUserId, listener);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new Decoration());
        refreshList();
    }

    public void refreshList() {
        layoutManager.scrollToPosition(0);
        refreshLayout.setRefreshing(true);
        onRefreshing();
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        layoutManager = new LinearLayoutManager(getContext());
        return layoutManager;
    }

    @Override
    protected void onRefreshing() {
        if (type == UserListType.followed) {
            userPrvr.getFolloweds();
        } else {
            userPrvr.getFans();
        }
    }

    @Override
    protected void onLoadMore() {
        isfirstPage = false;
        userPrvr.loadMoreData();
    }

    private void setRefreshState(final boolean state) {
        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(state);
            }
        }, 500);
    }

    public enum UserListType {
        followed,
        fans
    }

    static class Decoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, ScreenUtils.dp2px(1, parent.getContext()));
        }
    }
}
