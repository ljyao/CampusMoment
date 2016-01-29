package adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljy on 15/12/7.
 */
public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter {
    protected List<T> items = new ArrayList<>();

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void update(int position, T item) {
        this.items.set(position, item);
        notifyItemChanged(position);
    }

    public void append(List<T> items) {
        append(this.items.size(), items);
    }

    public void append(int position, List<T> items) {
        this.items.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public void append(int position, T item) {
        this.items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public List<T> getItems() {
        return items;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // additional methods to manipulate the items

    public T getItem(int position) {
        return items.get(position);
    }

}
