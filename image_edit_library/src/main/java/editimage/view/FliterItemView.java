package editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uy.imageeditlibrary.R;
import com.uy.util.Worker;

import java.io.File;

import adapter.ViewWrapper;
import editimage.fliter.FliterType;
import editimage.fliter.PhotoProcessing;
import editimage.fragment.FliterListFragment;
import helper.common_util.FileUtils;

/**
 * Created by shine on 16-2-19.
 */
public class FliterItemView extends RelativeLayout implements ViewWrapper.Binder<FliterType> {
    private static final String FLITER_DIR = "/fliter";
    private TextView fliterTv;
    private SimpleDraweeView fliterIv;
    private FliterType data;
    private FliterListFragment.FliterItemListener listener;

    public FliterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View itemView = inflate(getContext(), R.layout.fliter_itemview, this);
        fliterIv = (SimpleDraweeView) itemView.findViewById(R.id.fliter_image);
        fliterTv = (TextView) itemView.findViewById(R.id.fliter_tv);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(data);
            }
        });
    }

    @Override
    public void bind(final FliterType data) {

        this.data = data;
        fliterTv.setText(data.name);
        String fliterDirPath = FileUtils.getCacheDir(getContext()) + FLITER_DIR;
        File fliterDir = new File(fliterDirPath);
        if (!fliterDir.exists()) {
            fliterDir.mkdir();
        }
        final String filePath = fliterDirPath + "/" + data.ordinal();
        final File fliterFile = new File(filePath);
        if (fliterFile.exists()) {
            fliterIv.setImageURI(Uri.parse("file://" + filePath));
            return;
        }
        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap originalBmp = BitmapFactory.decodeResource(getResources(), R.drawable.fliter_original);
                    Bitmap bmp = PhotoProcessing.filterPhoto(originalBmp, data.ordinal());
                    FileUtils fileUtils = new FileUtils();
                    fileUtils.createBitmapFile(fliterFile, bmp);
                    Worker.postMain(new Runnable() {
                        @Override
                        public void run() {
                            fliterIv.setImageURI(Uri.parse("file://" + filePath));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setListener(FliterListFragment.FliterItemListener listener) {
        this.listener = listener;
    }
}
