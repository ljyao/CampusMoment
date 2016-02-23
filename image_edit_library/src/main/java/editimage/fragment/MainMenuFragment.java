package editimage.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.uy.imageeditlibrary.R;

import editimage.EditImageActivity;
import imagezoom.ImageViewTouchBase;


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

    private final class MenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == stickerBtn) {
                editImageActivity.mode = EditImageActivity.MODE_STICKERS;
                editImageActivity.mStirckerFragment.getmStickerView().setVisibility(View.VISIBLE);
                editImageActivity.bottomGallery.setCurrentItem(1);
                editImageActivity.bannerFlipper.showNext();
            } else if (v == fliterBtn) {
                editImageActivity.mode = EditImageActivity.MODE_FILTER;
                editImageActivity.mFliterListFragment.setCurrentBitmap(editImageActivity.mainBitmap);
                editImageActivity.mainImage.setImageBitmap(editImageActivity.mainBitmap);
                editImageActivity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
                editImageActivity.mainImage.setScaleEnabled(false);
                editImageActivity.bottomGallery.setCurrentItem(2);
                editImageActivity.bannerFlipper.showNext();
            } else if (v == cropBtn) {
                editImageActivity.mode = EditImageActivity.MODE_CROP;
                editImageActivity.mCropPanel.setVisibility(View.VISIBLE);
                editImageActivity.mainImage.setImageBitmap(editImageActivity.mainBitmap);
                editImageActivity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
                editImageActivity.bottomGallery.setCurrentItem(3);
                editImageActivity.mainImage.setScaleEnabled(false);// 禁用缩放
                //
                RectF r = editImageActivity.mainImage.getBitmapRect();
                editImageActivity.mCropPanel.setCropRect(r);
                // System.out.println(r.left + "    " + r.top);
                editImageActivity.bannerFlipper.showNext();
            } else if (v == rotateBtn) {
                editImageActivity.mode = EditImageActivity.MODE_ROTATE;
                editImageActivity.bottomGallery.setCurrentItem(4);
                editImageActivity.mainImage.setImageBitmap(editImageActivity.mainBitmap);
                editImageActivity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
                editImageActivity.mainImage.setVisibility(View.GONE);

                editImageActivity.mRotatePanel.addBit(editImageActivity.mainBitmap,
                        editImageActivity.mainImage.getBitmapRect());
                editImageActivity.mRotateFragment.mSeekBar.setProgress(0);
                editImageActivity.mRotatePanel.reset();
                editImageActivity.mRotatePanel.setVisibility(View.VISIBLE);
                editImageActivity.bannerFlipper.showNext();
            }
            editImageActivity.setViewPageHeight();
        }
    }


}
