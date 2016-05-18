
package community.fragment;

import android.app.ProgressDialog;
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
import community.providable.UserPrvdr;
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

    private FollowedsAndFansPrvdr followedsAndFansPrvdr;
    private UserPrvdr userPrvdr = new UserPrvdr();
    private boolean isfirstPage = true;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private NetLoaderListener<Boolean> followUserCallBack = new NetLoaderListener<Boolean>() {
        @Override
        public void onComplete(boolean statue, Boolean result) {
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    };
    private OnClickFollowListener followListener = new OnClickFollowListener() {
        @Override
        public void OnClickFollow(CommUser user) {
            progressDialog.show();
            userPrvdr.followUser(user, followUserCallBack);
        }

        @Override
        public void OnClickUnFollow(CommUser user) {
            progressDialog.show();
            userPrvdr.cancelFollowUser(user, followUserCallBack);
        }
    };
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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("操作中...");
        adapter = new UserAdapter();
        adapter.setFollowListener(followListener);
        followedsAndFansPrvdr = new FollowedsAndFansPrvdr(mUserId, listener);
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
            followedsAndFansPrvdr.getFolloweds();
        } else if (type == UserListType.fans) {
            followedsAndFansPrvdr.getFans();
        } else if (type == UserListType.Recommended) {
            followedsAndFansPrvdr.getRecommendedUsers();
        }
    }

    @Override
    protected void onLoadMore() {
        isfirstPage = false;
        followedsAndFansPrvdr.loadMoreData();
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
        fans,
        Recommended
    }

    public interface OnClickFollowListener {
        void OnClickFollow(CommUser user);

        void OnClickUnFollow(CommUser user);
    }

    static class Decoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, ScreenUtils.dp2px(1, parent.getContext()));
        }
    }
}
