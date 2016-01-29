package community.fragment;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.umeng.comm.core.beans.ImageItem;
import com.uy.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import community.adapter.AlbumsAdapter;
import community.providable.AlbumsPrvdr;
import community.providable.NetLoaderListener;
import fragment.RefreshRecycleFragment;

/**
 * Created by ljy on 16/1/4.
 */
public class AlbumsFragment extends RefreshRecycleFragment {
    private AlbumsAdapter albumsAdapter;
    private AlbumsPrvdr albumsPrvdr;
    private List<ImageItem> lists;
    private String userId;

    public void initView() {
        lists = new ArrayList<>();
        albumsAdapter = new AlbumsAdapter(lists);
        listView.setAdapter(albumsAdapter);
        listView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemSpace = ScreenUtils.dp2px(3, view.getContext());
                outRect.set(0, itemSpace, itemSpace, itemSpace);
            }
        });
        albumsPrvdr = new AlbumsPrvdr(userId);
        initData();
    }

    private void initData() {
        albumsPrvdr.getFirstPageData(new NetLoaderListener<List<ImageItem>>() {
            @Override
            public void onComplete(boolean statue, List<ImageItem> result) {
                albumsAdapter.update(result);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    protected void onRefreshing() {

    }

    @Override
    protected void onLoadMore() {
        albumsPrvdr.getNextPageData(new NetLoaderListener<List<ImageItem>>() {
            @Override
            public void onComplete(boolean statue, List<ImageItem> result) {
                albumsAdapter.append(result);
            }
        });
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
