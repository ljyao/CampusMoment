package convenientbanner.holder;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;


/**
 * Created by shine on 16-2-16.
 */
public class ImageHolder implements Holder<Uri> {
    private SimpleDraweeView draweeView;

    @Override
    public View createView(Context context) {
        draweeView = new SimpleDraweeView(context);
        draweeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return draweeView;
    }

    @Override
    public void UpdateUI(Context context, int position, Uri data) {
        draweeView.setImageURI(data);
    }
}
