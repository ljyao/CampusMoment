package editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 旋转图片
 *
 * @author 潘易
 */
public class RotateImageView extends View {
    private Rect srcRect;
    private RectF dstRect;
    private Rect maxRect;// 最大限制矩形框

    private Bitmap bitmap;
    private Matrix matrix = new Matrix();// 辅助计算矩形

    private float scale;// 缩放比率
    private int rotateAngle;

    private RectF wrapRect = new RectF();// 图片包围矩形
    private Paint bottomPaint;
    private RectF originImageRect;


    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    public void addBit(Bitmap bit, RectF imageRect) {
        bitmap = bit;
    }

    public void rotateImage(int angle) {
        rotateAngle = angle;

    }


}