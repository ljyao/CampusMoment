package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.uy.common.R;

import listener.RecycleScrollListener;
import views.MySwipeRefreshLayout;

/**
 * Created by ljy on 15/12/7.
 */
public abstract class RefreshRecycleFragment<T extends RecyclerView.Adapter> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    protected MySwipeRefreshLayout refreshLayout;
    protected RecyclerView listView;
    protected RelativeLayout emptyLayout;
    protected T adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refresh_recycle, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;

        try {
            listView = (RecyclerView) viewGroup.findViewById(android.R.id.list);
            listView.addOnScrollListener(new RecycleScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    RefreshRecycleFragment.this.onLoadMore();
                }
            });
            emptyLayout = (RelativeLayout) viewGroup.findViewById(R.id.empty_view_holder);
            refreshLayout = (MySwipeRefreshLayout) viewGroup.findViewById(R.id.id_refresh);
            refreshLayout.setColorSchemeResources(R.color.pull_to_refresh_color);
            refreshLayout.setOnRefreshListener(this);
            listView.setLayoutManager(getLayoutManager());
            listView.setItemAnimator(getItemAnimator());
            refreshLayout.setStartDependView(listView);
            refreshLayout.setEntryAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(adapter);

    }

    abstract protected RecyclerView.LayoutManager getLayoutManager();

    public RecyclerView.ItemAnimator getItemAnimator() {
        return new DefaultItemAnimator();
    }

    protected abstract void onRefreshing();

    protected abstract void onLoadMore();

    @Override
    public void onRefresh() {
        onRefreshing();
    }
}
