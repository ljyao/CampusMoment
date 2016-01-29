package community.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.ImageItem;

import java.util.List;

/**
 * Created by ljy on 16/1/4.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    private List<ImageItem> items;

    public AlbumsAdapter(List<ImageItem> list) {
        items = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleDraweeView draweeView = new SimpleDraweeView(parent.getContext());
        return new ViewHolder(parent, draweeView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.draweeView.setImageURI(Uri.parse(items.get(position).middleImageUrl));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<ImageItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void update(int position, ImageItem item) {
        this.items.set(position, item);
        notifyItemChanged(position);
    }

    public void append(List<ImageItem> items) {
        append(this.items.size(), items);
    }

    public void append(int position, List<ImageItem> items) {
        this.items.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView draweeView;

        public ViewHolder(View itemView, SimpleDraweeView view) {
            super(itemView);
            draweeView = view;
        }
    }
}
