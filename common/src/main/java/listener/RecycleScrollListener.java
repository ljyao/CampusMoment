package listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.AbsListView;

/**
 * Created by ljy on 15/12/21.
 */
public abstract class RecycleScrollListener extends RecyclerView.OnScrollListener {
    private boolean loadEnd;
    private boolean mLastItemVisible;
    private int visibleThreshold = 8;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = 0;
        int totalItemCount = 0;
        int firstVisibleItem = 0;
        Object layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            visibleItemCount = linearLayoutManager.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            visibleItemCount = staggeredGridLayoutManager.getChildCount();
            totalItemCount = staggeredGridLayoutManager.getItemCount();
            int into[] = null;
            into = staggeredGridLayoutManager.findFirstVisibleItemPositions(into);
            firstVisibleItem = into[0];
        }

        mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - visibleThreshold);

        loadEnd = (totalItemCount > 0) && (firstVisibleItem == totalItemCount - 1);
    }

    public abstract void onLoadMore(int page, int totalItemsCount);

    public void onLoadEnd(boolean status) {

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {
            onLoadMore(0, 0);
        }

        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            onLoadEnd(loadEnd);
        }
    }
}
