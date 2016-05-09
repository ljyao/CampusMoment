package editimage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import editimage.model.ImageScaleType;

/**
 * Created by Shine on 2016/5/1.
 */
public class ImageEditContainer extends RelativeLayout {
    private ImageScaleType scaleType;

    public ImageEditContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScaleType(ImageScaleType scaleType) {
        this.scaleType = scaleType;
        int width = getMeasuredWidth();
        int height = (int) (width /
                scaleType.scale);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    public void getScaleType() {
    }
}
