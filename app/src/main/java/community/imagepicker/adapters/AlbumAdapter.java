
package community.imagepicker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import community.imagepicker.model.AlbumModel;
import community.imagepicker.widgets.AlbumItemView;

public class AlbumAdapter extends MBaseAdapter<AlbumModel> {

    public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
        super(context, models);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumItemView albumItem = null;
        if (convertView == null) {
            albumItem = new AlbumItemView(mContext);
            convertView = albumItem;
        } else {
            albumItem = (AlbumItemView) convertView;
        }
        albumItem.update(mDataSet.get(position));
        return convertView;
    }

}
