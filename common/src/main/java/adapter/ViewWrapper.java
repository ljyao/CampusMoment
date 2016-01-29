package adapter;

/**
 * Created by ljy on 15/12/7.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ViewWrapper<T, V extends View & ViewWrapper.Binder<T>> extends RecyclerView.ViewHolder {

    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }

    public interface Binder<T> {
        void bind(T data);
    }
}