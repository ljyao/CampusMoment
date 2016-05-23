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
        //新的比例
        this.scaleType = scaleType;
        //获取控件宽
        int width = getMeasuredWidth();
        //在图片比例不变下，算出新的高度
        int height = (int) (width / scaleType.scale);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        //新的宽度
        layoutParams.width = width;
        //新的高度
        layoutParams.height = height;
        //设置控件新的宽度和高度
        setLayoutParams(layoutParams);
    }

    public void getScaleType() {
    }
}
