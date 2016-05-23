package community.views;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 方形ImageView,宽高相等
 */
public class SquareImageView extends SimpleDraweeView {

    /**
     * 记录控件的宽和高
     */
    protected Point mSize = new Point();

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int imgWidth = getMeasuredWidth();
        int imgHeight = getMeasuredHeight();
        // int sideLength = Math.max(imgWidth, imgHeight);
        // 设置图片大小
        setMeasuredDimension(imgWidth, imgWidth);
        mSize.x = imgWidth;
        mSize.y = imgHeight;
    }

}
