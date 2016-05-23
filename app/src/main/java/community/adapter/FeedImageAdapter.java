package community.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.ui.adapters.CommonAdapter;
import com.umeng.comm.ui.adapters.viewholders.NullViewParser;

import community.views.SquareImageView;


/**
 * 消息流九宫格图片的Adapter
 */
public class FeedImageAdapter extends CommonAdapter<ImageItem, NullViewParser> {

    private ImgDisplayOption mDisplayOption = new ImgDisplayOption();

    /**
     * 构造函数
     *
     * @param context
     */
    public FeedImageAdapter(Context context) {
        super(context);
        initDisplayOption();
    }

    @Override
    protected NullViewParser createViewHolder() {
        return new NullViewParser();
    }

    /**
     * 初始化显示的图片跟配置</br>
     */
    private void initDisplayOption() {
        mDisplayOption.mLoadingResId = ResFinder.getResourceId(ResType.DRAWABLE,
                "umeng_comm_not_found");
        mDisplayOption.mLoadFailedResId = ResFinder.getResourceId(ResType.DRAWABLE,
                "umeng_comm_not_found");
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 9 ? 9 : count;
    }

    // 此处不用ViewParser的方式，主要是只有ImageView且都需要设置Tag（ViewHolder跟图片的url地址，导致冲突）
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        SquareImageView simpleDraweeView;
        if (view == null) {
            LayoutParams mImageViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            simpleDraweeView = new SquareImageView(mContext);
            simpleDraweeView.setScaleType(ScaleType.CENTER_CROP);
            simpleDraweeView.setLayoutParams(mImageViewLayoutParams);
        } else {
            simpleDraweeView = (SquareImageView) view;
        }
        simpleDraweeView.setImageURI(Uri.parse(getItem(position).originImageUrl));
        return simpleDraweeView;
    }

    @Override
    protected void setItemData(int position, NullViewParser holder, View rootView) {
    }
}
