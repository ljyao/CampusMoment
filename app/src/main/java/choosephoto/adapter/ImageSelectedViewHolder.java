package choosephoto.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.constants.Constants;
import com.uy.bbs.R;

import community.activity.PostFeedActivity;


/**
 * 发布Feed时已选中图片的GridView ViewHolder
 */
public class ImageSelectedViewHolder extends RecyclerView.ViewHolder {
    private final Context context;
    private PostFeedActivity.OnItemClickListener onItemClickListener;
    private SimpleDraweeView imageView;// 选中的图片
    private ImageView deleteImageView;// 选中图片右上方的删除图标
    private ImageSelectedAdapter.ImageSelectedListener listener;
    private String path;

    public ImageSelectedViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        imageView = (SimpleDraweeView) itemView.findViewById(R.id.image_selected);
        deleteImageView = (ImageView) itemView.findViewById(R.id.image_delete);
        deleteImageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(path);
            }
        });
    }

    protected void setItemData(String path) {
        this.path = path;
        imageView.setTag(path);
        if (!path.equals(Constants.ADD_IMAGE_PATH_SAMPLE)) {
            displayImage(path);
        } else {
            Drawable drawable = context.getResources().getDrawable(R.drawable.umeng_comm_add_image);
            showDeleteImage(drawable);
        }
    }

    private void displayImage(final String path) {
        deleteImageView.setVisibility(View.VISIBLE);
        // 加载图片
        Uri uri = Uri.parse("file://" + path);
        imageView.setImageURI(uri);
        // 删除按钮
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showDeleteItemDialog(path);
            }
        });
    }

    public void setOnItemClickListener(PostFeedActivity.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void showDeleteImage(Drawable drawable) {
        imageView.setImageDrawable(drawable);
        deleteImageView.setVisibility(View.GONE);
    }

}
