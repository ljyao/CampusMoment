package choosephoto.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.comm.core.constants.Constants;
import com.uy.bbs.R;

import java.util.List;

import community.activity.PostFeedActivity;

/**
 * 发布消息时选中的图片预览的GridView适配器, 用户可以删除选中的图片.
 */
public class ImageSelectedAdapter extends RecyclerView.Adapter<ImageSelectedViewHolder> {
    private Context mContext;
    private List<String> list;
    private PostFeedActivity.OnItemClickListener onItemClickListener;

    private ImageSelectedAdapter.ImageSelectedListener listener = new ImageSelectedListener() {
        @Override
        public void showDeleteItemDialog(final String path) {
            String msg = mContext.getString(R.string.umeng_comm_delete_photo);
            String confirmText = mContext.getString(R.string.umeng_comm_text_confirm);
            String cancelText = mContext.getString(R.string.umeng_comm_text_cancel);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(msg).setPositiveButton(confirmText,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.remove(path);
                            // 添加add图标
                            if (!list.contains(Constants.ADD_IMAGE_PATH_SAMPLE)) {
                                list.add(0, Constants.ADD_IMAGE_PATH_SAMPLE);
                            }
                            notifyDataSetChanged();
                        }
                    });

            builder.setNegativeButton(cancelText, null);
            builder.create().show();
        }
    };

    public ImageSelectedAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }


    @Override
    public ImageSelectedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewGroup = View.inflate(parent.getContext(), R.layout.image_selected_item, null);
        ImageSelectedViewHolder viewHolder = new ImageSelectedViewHolder(viewGroup);
        viewHolder.setOnItemClickListener(onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageSelectedViewHolder holder, int position) {
        holder.setItemData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public List<String> getDataSource() {
        return list;
    }

    public void setOnItemClickListener(PostFeedActivity.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateData(List<String> selectedList) {
        list = selectedList;
        notifyDataSetChanged();
    }

    public void addToFirst(String addImagePathSample) {
        list.add(0, addImagePathSample);
    }

    public interface ImageSelectedListener {
        void showDeleteItemDialog(String path);
    }
}
