
package choosephoto.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.ui.imagepicker.model.PhotoModel;
import com.umeng.comm.ui.imagepicker.polites.GestureImageView;
import com.uy.bbs.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


/**
 * 图片浏览视图
 */
@EViewGroup(R.layout.view_photopreview)
public class PhotoPreview extends LinearLayout {
    @ViewById(R.id.prephoto_itemview)
    public GestureImageView mImageView;
    private OnClickListener mClickListener;

    public PhotoPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Click(R.id.prephoto_itemview)
    public void onClickItemView() {
        if (mClickListener != null) {
            mClickListener.onClick(mImageView);
        }
    }

    public void loadImage(PhotoModel photoModel) {
        loadImage("file://" + photoModel.getOriginalPath());
    }

    private void loadImage(String path) {
        ImageLoaderManager.getInstance().getCurrentSDK().displayImage(path, mImageView);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }


}
