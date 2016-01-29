package choosephoto.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ljy on 15/12/30.
 */
public class WrapHeightImageView extends ImageView {
    private float scale = 0;

    public WrapHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width;
        if (scale != 0) {
            height = (int) (width / scale);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        int bmpHeight = bm.getHeight();
        int bmpWidth = bm.getWidth();
        scale = (float) bmpWidth / (float) bmpHeight;
        requestLayout();
    }
}
