package camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uy.bbs.R;

import java.util.List;

import camera.effect.FilterEffect;
import camera.util.GPUImageFilterTools;
import gpuimage.GPUImageFilter;
import gpuimage.GPUImageView;


public class FilterAdapter extends BaseAdapter {

    List<FilterEffect> filterUris;
    Context mContext;
    private Bitmap background;

    private int selectFilter = 0;

    public FilterAdapter(Context context, List<FilterEffect> effects, Bitmap backgroud) {
        filterUris = effects;
        mContext = context;
        this.background = backgroud;
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
    }

    @Override
    public int getCount() {
        return filterUris.size();
    }

    @Override
    public Object getItem(int position) {
        return filterUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EffectHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_bottom_filter, null);
            holder = new EffectHolder();
            holder.filteredImg = (GPUImageView) convertView.findViewById(R.id.small_filter);
            holder.filterName = (TextView) convertView.findViewById(R.id.filter_name);
            convertView.setTag(holder);
        } else {
            holder = (EffectHolder) convertView.getTag();
        }

        final FilterEffect effect = (FilterEffect) getItem(position);

        holder.filteredImg.setImage(background);
        holder.filterName.setText(effect.getTitle());
        //if (!effect.isOri() && effect.getType() != null) {
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(mContext, effect.getType());
        holder.filteredImg.setFilter(filter);

        return convertView;
    }

    class EffectHolder {
        GPUImageView filteredImg;
        TextView filterName;
    }

}
