package choosephoto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uy.bbs.R;
import com.uy.util.CompressType;

import java.util.ArrayList;
import java.util.List;

import choosephoto.util.ImageLoader;
import choosephoto.views.WrapHeightImageView;

public class PhotoWallAdapter extends RecyclerView.Adapter {
    private final ArrayList<String> imagesSelected = new ArrayList<>();
    private Context context;
    private ArrayList<String> imagePathList = null;
    private ImageLoader loader;
    private SparseBooleanArray selectionMap;
    private boolean singleChoose;

    public PhotoWallAdapter(Context context, ArrayList<String> imagePathList, boolean singleChoose) {
        this.context = context;
        this.imagePathList = imagePathList;
        loader = ImageLoader.getInstance(context);
        selectionMap = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.photo_wall_item, null);
        final PhotoViewHolder viewHolder = new PhotoViewHolder(itemView);
        viewHolder.imageView = (WrapHeightImageView) itemView.findViewById(R.id.photo_wall_item_photo);
        viewHolder.checkBox = (ImageView) itemView.findViewById(R.id.photo_wall_item_cb);
        viewHolder.imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.isChecked) {
                    viewHolder.isChecked = false;
                    viewHolder.checkBox.setBackgroundResource(R.drawable.compose_photo_preview_default);
                    viewHolder.imageView.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));

                } else {
                    viewHolder.isChecked = true;
                    viewHolder.checkBox.setBackgroundResource(R.drawable.compose_photo_preview_right);
                    viewHolder.imageView.setColorFilter(null);
                }
                if (singleChoose) {
                    int oldSelected = -1;
                    for (int i = 0; i < imagePathList.size(); i++) {
                        if (selectionMap.get(i)) {
                            oldSelected = i;
                            break;
                        }
                    }
                    selectionMap.clear();
                    if (oldSelected != -1) {
                        notifyItemChanged(oldSelected);
                    }
                }
                selectionMap.put(viewHolder.position, viewHolder.isChecked);
                notifyItemChanged(viewHolder.position);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoViewHolder mHolder = (PhotoViewHolder) holder;
        mHolder.position = position;
        mHolder.isChecked = selectionMap.get(position);
        mHolder.filePath = imagePathList.get(position);
        if (mHolder.isChecked) {
            mHolder.checkBox.setBackgroundResource(R.drawable.compose_photo_preview_right);
            mHolder.imageView.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
        } else {
            mHolder.checkBox.setBackgroundResource(R.drawable.compose_photo_preview_default);
            mHolder.imageView.setColorFilter(null);

        }
        mHolder.imageView.setTag(mHolder.filePath);
        loader.loadImage(CompressType.COMPRESS_DP, 80, mHolder.filePath, mHolder.imageView);
    }


    @Override
    public int getItemCount() {
        return imagePathList == null ? 0 : imagePathList.size();
    }


    public SparseBooleanArray getSelectionMap() {
        return selectionMap;
    }

    public void clearSelectionMap() {
        selectionMap.clear();
    }

    public void setSingleChoose(boolean singleChoose) {
        this.singleChoose = singleChoose;
    }

    public void addImagesSelected(ArrayList<String> images) {
        if (images == null || images.size() == 0)
            return;
        imagesSelected.addAll(images);
        removeEqualItem(imagesSelected);
        for (String path : imagesSelected) {
            int key = imagePathList.indexOf(path);
            if (key >= 0)
                selectionMap.put(key, true);
        }
    }

    private void removeEqualItem(List<String> list) {
        for (String path : list) {
            int key = list.indexOf(path);
            if (key >= 0)
                selectionMap.put(key, true);
        }
    }

    public void updateData(ArrayList<String> list) {
        if (list == null || list.size() == 0) {
            selectionMap.clear();
            return;
        }
        imagePathList = list;
        selectionMap.clear();
        for (String path : imagesSelected) {
            int key = imagePathList.indexOf(path);
            if (key >= 0)
                selectionMap.put(key, true);
        }
        notifyDataSetChanged();
    }

    // 获取已选择的图片路径
    public ArrayList<String> getSelectImagePaths() {
        if (selectionMap.size() == 0) {
            return null;
        }
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < imagePathList.size(); i++) {
            if (selectionMap.get(i)) {
                images.add(imagePathList.get(i));
            }
        }
        imagesSelected.addAll(images);
        removeEqualItem(imagesSelected);
        return imagesSelected;
    }

    private static class PhotoViewHolder extends RecyclerView.ViewHolder {
        WrapHeightImageView imageView;
        ImageView checkBox;
        String filePath;
        boolean isChecked;
        int position;

        public PhotoViewHolder(View itemView) {
            super(itemView);
        }
    }
}
