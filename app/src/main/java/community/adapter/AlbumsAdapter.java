package community.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.ImageItem;

import java.util.ArrayList;
import java.util.List;

import helper.common_util.ScreenUtils;

/**
 * Created by ljy on 16/1/4.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    private OnClickItemListener onClickItemListener;
    private List<ImageItem> items;

    public AlbumsAdapter(List<ImageItem> list) {
        items = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleDraweeView draweeView = new SimpleDraweeView(parent.getContext());
        draweeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int width = ScreenUtils.getScreenW(parent.getContext()) / 3;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, width);
        draweeView.setLayoutParams(layoutParams);
        return new ViewHolder(draweeView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(position);
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

    public ArrayList<String> getPhotos() {
        ArrayList<String> photos = new ArrayList<>();
        for (ImageItem item : items) {
            photos.add(item.originImageUrl);
        }
        return photos;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }


    public interface OnClickItemListener {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView draweeView;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView;
            draweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItemListener != null)
                        onClickItemListener.onClick(position);
                }
            });
        }

        public void setData(int position) {
            this.position = position;
            draweeView.setImageURI(Uri.parse(items.get(position).middleImageUrl));
        }
    }
}
