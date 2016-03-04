
package community.imagepicker.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;

import community.imagepicker.model.PhotoModel;
import community.imagepicker.widgets.PhotoItemViewHolder;


/**
 * 图片选择Adapter
 */
public class PhotoAdapter extends MBaseAdapter<PhotoModel> {
    private PhotoItemViewHolder.onPhotoItemCheckedListener listener;
    private PhotoItemViewHolder.onItemClickListener mClickListener;

    private PhotoAdapter(Context context, ArrayList<PhotoModel> models) {
        super(context, models);
    }

    public PhotoAdapter(Context context, ArrayList<PhotoModel> models, int screenWidth,
                        PhotoItemViewHolder.onPhotoItemCheckedListener listener, PhotoItemViewHolder.onItemClickListener clickListener) {
        this(context, models);
        this.listener = listener;
        this.mClickListener = clickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PhotoItemViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new PhotoItemViewHolder(mContext, parent, listener);
            convertView = viewHolder.getItemView();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PhotoItemViewHolder) convertView.getTag();
        }
        final PhotoModel photoModel = getItem(position);
        viewHolder.setPhotoModel(photoModel);
        viewHolder.setSelected(photoModel.isChecked());
        viewHolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position);
                }
            }
        });
        return convertView;
    }

    public void setOnItemClickListener(PhotoItemViewHolder.onItemClickListener clickListener) {
        this.mClickListener = clickListener;
    }
}
