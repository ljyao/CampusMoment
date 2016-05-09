package editimage.adapter;


import android.view.ViewGroup;

import java.util.List;

import adapter.RecycleAdapter;
import editimage.fliter.FliterType;
import editimage.fragment.FliterListFragment;
import editimage.view.FliterItemView;

/**
 * Created by shine on 16-2-19.
 */
public class FliterAdapter extends RecycleAdapter<FliterType, FliterItemView> {
    private FliterListFragment.FliterItemListener listener;

    public FliterAdapter(List<FliterType> datas, FliterListFragment.FliterItemListener listener) {
        items = datas;
        this.listener = listener;
    }

    @Override
    protected FliterItemView onCreateItemView(ViewGroup parent, int viewType) {
        FliterItemView itemVIew = new FliterItemView(parent.getContext(), null);
        itemVIew.setListener(listener);
        return itemVIew;
    }

}
