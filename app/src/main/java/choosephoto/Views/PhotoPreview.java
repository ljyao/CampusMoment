
package choosephoto.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import imagezoom.ImageViewTouch;
import imagezoom.ImageViewTouchBase;


/**
 * 图片浏览视图
 */
@EViewGroup(R.layout.view_photopreview)
public class PhotoPreview extends LinearLayout {
    @ViewById(R.id.preview_photo_view)
    public ImageViewTouch mImageView;
    private OnClickListener mClickListener;

    public PhotoPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    public void initViews() {
        mImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    }

    @Click(R.id.preview_photo_view)
    public void onClickItemView() {
        if (mClickListener != null) {
            mClickListener.onClick(mImageView);
        }
    }

    public void loadImage(String path) {
        ImageLoader.getInstance().displayImage(path, mImageView);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }


}
