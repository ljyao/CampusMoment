package imagezoom.graphics;

import android.graphics.Bitmap;

import imagezoom.ImageViewTouchBase;

/**
 * Base interface used in the {@link ImageViewTouchBase} view
 *
 * @author alessandro
 */
public interface IBitmapDrawable {

    Bitmap getBitmap();
}
