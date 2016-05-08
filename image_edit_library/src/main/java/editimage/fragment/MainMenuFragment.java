package editimage.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.jni.bitmap_operations.JniBitmapHolder;
import com.uy.imageeditlibrary.R;
import com.uy.util.Worker;

import editimage.EditImageActivity;


/**
 * 工具栏主菜单
 */
public class MainMenuFragment extends Fragment {
    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;
    private EditImageActivity editImageActivity;

    private View stickerBtn;// 贴图按钮
    private View fliterBtn;// 滤镜按钮
    private View cropBtn;// 剪裁按钮
    private View rotateBtn;// 旋转按钮
    private int angle = 0;

    public static MainMenuFragment newInstance(EditImageActivity activity) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.editImageActivity = activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu, null);
        stickerBtn = mainView.findViewById(R.id.btn_stickers);
        fliterBtn = mainView.findViewById(R.id.btn_fliter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MenuClick menuClick = new MenuClick();
        stickerBtn.setOnClickListener(menuClick);
        fliterBtn.setOnClickListener(menuClick);
        cropBtn.setOnClickListener(menuClick);
        rotateBtn.setOnClickListener(menuClick);
    }

    public void changeChooseStatus(int mode) {
        stickerBtn.setSelected(false);
        fliterBtn.setSelected(false);
        cropBtn.setSelected(false);
        rotateBtn.setSelected(false);
        switch (mode) {
            case EditImageActivity.MODE_STICKERS:
                stickerBtn.setSelected(true);
                break;
            case EditImageActivity.MODE_FILTER:
                break;
            case EditImageActivity.MODE_CROP:
                break;
            case EditImageActivity.MODE_ROTATE:
                break;
        }
        stickerBtn.setSelected(true);
    }

    public void backToMain() {
        editImageActivity.currentMode = EditImageActivity.MODE_NONE;
        editImageActivity.setCurrentItem(0);
        editImageActivity.mainImage.setVisibility(View.VISIBLE);
        editImageActivity.mStickerView.setVisibility(View.VISIBLE);
        editImageActivity.mainImage.setVisibility(View.VISIBLE);
        editImageActivity.bannerFlipper.showPrevious();
    }

    private final class MenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            int mode = EditImageActivity.MODE_NONE;
            if (v == stickerBtn) {
                mode = EditImageActivity.MODE_STICKERS;
                editImageActivity.setCurrentMode(mode);
            } else if (v == fliterBtn) {
                mode = EditImageActivity.MODE_FILTER;
                editImageActivity.setCurrentMode(mode);
            } else if (v == cropBtn) {
                mode = EditImageActivity.MODE_CROP;
                editImageActivity.setCurrentMode(mode);
            } else if (v == rotateBtn) {
                mode = EditImageActivity.MODE_ROTATE;
                editImageActivity.setCurrentMode(mode);
                angle += 90;
                angle = angle % 360;
                editImageActivity.progressBar.setVisibility(View.VISIBLE);
                Worker.postExecuteTask(new Runnable() {
                    @Override
                    public void run() {
                        JniBitmapHolder jniBitmapHolder = new JniBitmapHolder();
                        jniBitmapHolder.storeBitmap(editImageActivity.mainBitmap);
                        jniBitmapHolder.rotateBitmapCw90();
                        Bitmap newBmp = jniBitmapHolder.getBitmapAndFree();
                        editImageActivity.setEditBitmap(newBmp);
                    }
                });

            }

        }
    }

}
