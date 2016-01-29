package com.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chat.util.SDCardImageLoader;
import com.uy.chat.R;
import com.uy.util.CompressType;

import java.util.ArrayList;

public class PhotoWallAdapter extends RecyclerView.Adapter {
    private Context context;

    private ArrayList<String> imagePathList = null;

    private SDCardImageLoader loader;

    private SparseBooleanArray selectionMap;

    public PhotoWallAdapter(Context context, ArrayList<String> imagePathList) {
        this.context = context;
        this.imagePathList = imagePathList;

        loader = new SDCardImageLoader(context);
        selectionMap = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.chat_photo_wall_item, null);
        final PhotoViewHolder viewHolder = new PhotoViewHolder(itemView);
        viewHolder.imageView = (ImageView) itemView.findViewById(R.id.photo_wall_item_photo);
        viewHolder.checkBox = (ImageView) itemView.findViewById(R.id.photo_wall_item_cb);
        viewHolder.imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.isChecked) {
                    viewHolder.isChecked = false;
                    viewHolder.checkBox.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.isChecked = true;
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
                }
                selectionMap.put(viewHolder.position, viewHolder.isChecked);
                if (viewHolder.isChecked) {
                    viewHolder.imageView.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
                } else {
                    viewHolder.imageView.setColorFilter(null);
                }

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
            mHolder.checkBox.setVisibility(View.VISIBLE);
        } else {
            mHolder.checkBox.setVisibility(View.INVISIBLE);
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

    private static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView checkBox;
        String filePath;
        boolean isChecked;
        int position;

        public PhotoViewHolder(View itemView) {
            super(itemView);
        }
    }
}
