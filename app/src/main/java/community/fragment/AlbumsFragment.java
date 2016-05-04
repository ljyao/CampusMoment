package community.fragment;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.ImageItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import java.util.ArrayList;
import java.util.List;

import choosephoto.activity.PhotoPreviewActivity_;
import community.adapter.AlbumsAdapter;
import community.providable.AlbumsPrvdr;
import community.providable.NetLoaderListener;
import fragment.RefreshRecycleFragment;
import helper.common_util.ScreenUtils;

/**
 * Created by ljy on 16/1/4.
 */
@EFragment
public class AlbumsFragment extends RefreshRecycleFragment<AlbumsAdapter> {
    @FragmentArg
    public CommUser user;
    private AlbumsPrvdr albumsPrvdr;
    private List<ImageItem> lists;
    private boolean isHideRefreshLayout = false;

    @AfterViews
    public void initView() {
        if (isHideRefreshLayout) {
            refreshLayout.setVisibility(View.GONE);
        }
        lists = new ArrayList<>();
        adapter = new AlbumsAdapter(lists);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemSpace = ScreenUtils.dp2px(1, view.getContext());
                outRect.set(itemSpace, itemSpace, itemSpace, itemSpace);
                int index = parent.indexOfChild(view);
                if (index < 3) {
                    outRect.top = 0;
                }
                if (index % 3 == 0) {
                    outRect.left = 0;
                }
                if (index % 3 == 2) {
                    outRect.right = 0;
                }
            }
        });
        adapter.setOnClickItemListener(new AlbumsAdapter.OnClickItemListener() {
            @Override
            public void onClick(int position) {
                ArrayList<String> photos = adapter.getPhotos();
                PhotoPreviewActivity_.intent(getContext()).current(position)
                        .title(user.name + "相册")
                        .photos(photos).start();
            }
        });
        loadData();
    }

    private void loadData() {
        if (user == null)
            return;
        albumsPrvdr = new AlbumsPrvdr(user.id);
        refreshLayout.setRefreshing(true);
        albumsPrvdr.getFirstPageData(new NetLoaderListener<List<ImageItem>>() {
            @Override
            public void onComplete(boolean statue, List<ImageItem> result) {
                refreshLayout.setRefreshing(false);
                adapter.update(result);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
    }

    @Override
    protected void onRefreshing() {
        loadData();
    }

    @Override
    protected void onLoadMore() {
        albumsPrvdr.getNextPageData(new NetLoaderListener<List<ImageItem>>() {
            @Override
            public void onComplete(boolean statue, List<ImageItem> result) {
                adapter.append(result);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void setUserId(CommUser user) {
        this.user = user;
        albumsPrvdr = new AlbumsPrvdr(user.id);
        loadData();
    }

    public void hideRefresh() {
        isHideRefreshLayout = true;
        if (isHideRefreshLayout && refreshLayout != null) {
            refreshLayout.setVisibility(View.GONE);
        }
    }

}
