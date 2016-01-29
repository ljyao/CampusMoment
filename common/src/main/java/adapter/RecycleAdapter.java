package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ljy on 15/12/7.
 */
public abstract class RecycleAdapter<T, V extends View & ViewWrapper.Binder<T>> extends BaseRecycleAdapter<T> {
    @Override
    public final ViewWrapper<T, V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<>(onCreateItemView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewWrapper) {
            ViewWrapper<T, V> viewWrapper = (ViewWrapper<T, V>) viewHolder;
            V view = viewWrapper.getView();
            T data = items.get(position);
            view.bind(data);
        }
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);
}
