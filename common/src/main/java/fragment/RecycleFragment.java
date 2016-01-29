package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.uy.common.R;


/**
 * Created by ljy on 15/12/7.
 */
public abstract class RecycleFragment extends Fragment {
    protected RecyclerView listView;
    protected RelativeLayout emptyLayout;
    protected RecyclerView.Adapter<?> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_base, container, false);

        onRefresh();
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;

        try {
            listView = (RecyclerView) viewGroup.findViewById(android.R.id.list);
            emptyLayout = (RelativeLayout) viewGroup.findViewById(R.id.empty_view_holder);
            listView.setLayoutManager(getLayoutManager());

            listView.setItemAnimator(getItemAnimator());

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

    protected abstract void onRefresh();
}
