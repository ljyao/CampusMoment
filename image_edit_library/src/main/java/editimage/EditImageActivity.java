package editimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.uy.imageeditlibrary.R;
import com.uy.util.Worker;

import editimage.fragment.CropFragment;
import editimage.fragment.FliterListFragment;
import editimage.fragment.MainMenuFragment;
import editimage.fragment.RotateFragment;
import editimage.fragment.StickerFragment;
import editimage.model.ImageScaleType;
import editimage.utils.BitmapUtils;
import editimage.view.CropImageView;
import editimage.view.ImageEditContainer;
import editimage.view.RotateImageView;
import editimage.view.StickerView;
import helper.common_util.ImageUtils;
import helper.common_util.ScreenUtils;
import imagezoom.ImageViewTouch;
import imagezoom.ImageViewTouchBase;


/**
 * 图片编辑 主页面
 * <p/>
 * 包含 1.贴图 2.滤镜 3.剪裁 4.底图旋转 功能
 */
public class EditImageActivity extends BaseActivity {
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";

    public static final int MODE_NONE = 0;
    /**
     * 贴图模式
     */
    public static final int MODE_STICKERS = 1;
    /**
     * 滤镜模式
     */
    public static final int MODE_FILTER = 2;
    /**
     * 剪裁模式
     */
    public static final int MODE_CROP = 3;
    /**
     * 文字模式
     */
    public static final int MODE_TEXT = 4;
    /**
     * 旋转模式
     */
    public static final int MODE_ROTATE = 5;

    public String filePath;// 需要编辑图片路径
    public String saveFilePath;// 生成的新图片路径
    public int mode = MODE_NONE;// 当前操作模式
    public Bitmap mainBitmap;// 底层显示Bitmap
    public ImageViewTouch mainImage;
    public ViewFlipper bannerFlipper;
    public StickerView mStickerView;// 贴图层View
    public CropImageView mCropPanel;// 剪切操作控件
    public RotateImageView mRotatePanel;// 旋转操作控件
    public StickerFragment mStirckerFragment;// 贴图Fragment
    public FliterListFragment mFliterListFragment;// 滤镜FliterListFragment
    public RotateFragment mRotateFragment;// 图片旋转Fragment
    public View progressBar;
    private int imageWidth, imageHeight;// 展示图片控件 宽 高
    private EditImageActivity mContext;
    private View backBtn;
    private View applyBtn;// 应用按钮
    private View saveBtn;// 保存按钮
    private MainMenuFragment mMainMenuFragment;// Menu
    private CropFragment mCropFragment;// 图片剪裁Fragment
    private FrameLayout menuFrameLayout;
    private Fragment currentFragment;
    private ImageEditContainer imageEditContainer;
    private View saveImageLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        initView();
        getData();
    }

    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);// 保存图片路径
        boolean isFromCache = getIntent().getBooleanExtra("isFromCache", false);
        if (isFromCache) {
            String key = getIntent().getStringExtra("key");
            loadImageFromCache(key);
        } else {
            loadImage(filePath);
        }
    }

    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = (int) ((float) metrics.widthPixels / 1.5);
        imageHeight = (int) ((float) metrics.heightPixels / 1.5);

        bannerFlipper = (ViewFlipper) findViewById(R.id.banner_flipper);
        bannerFlipper.setInAnimation(this, R.anim.in_bottom_to_top);
        bannerFlipper.setOutAnimation(this, R.anim.out_bottom_to_top);
        applyBtn = findViewById(R.id.apply);
        applyBtn.setOnClickListener(new ApplyBtnClick());
        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new SaveBtnClick());

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
        backBtn = findViewById(R.id.back_btn);// 退出按钮
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                forceReturnBack();
            }
        });

        mStickerView = (StickerView) findViewById(R.id.sticker_panel);
        mCropPanel = (CropImageView) findViewById(R.id.crop_panel);
        mRotatePanel = (RotateImageView) findViewById(R.id.rotate_panel);

        menuFrameLayout = (FrameLayout) findViewById(R.id.bottom_menu);
        mMainMenuFragment = MainMenuFragment.newInstance(this);
        mStirckerFragment = StickerFragment.newInstance(this);
        mFliterListFragment = FliterListFragment.newInstance(this);
        mCropFragment = CropFragment.newInstance(this);
        mRotateFragment = RotateFragment.newInstance(this);
        saveImageLayout = findViewById(R.id.save_image_layout);
        progressBar = findViewById(R.id.progressbar);
        imageEditContainer = (ImageEditContainer) findViewById(R.id.image_edit_container);
        setCurrentItem(mode);
    }

    /**
     * 异步载入编辑图片
     *
     * @param filepath
     */
    public void loadImage(final String filepath) {
        progressBar.setVisibility(View.VISIBLE);
        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                BitmapUtils.BitmapSize size = BitmapUtils.getBitmapSize(filepath);
                double width = size.width;
                double height = size.height;
                Bitmap bmp = BitmapFactory.decodeFile(filepath);
                if (width > 4000 || height > 4000) {
                    bmp = BitmapUtils.getSampledBitmap(filepath, 2);
                }
                setEditBitmap(bmp);
            }
        });
    }

    public void loadImageFromCache(final String key) {
        Bitmap bitmap = ImageUtils.RemoveBitmap(key);
        setEditBitmap(bitmap);
    }

    public void setEditBitmap(final Bitmap bmp) {
        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                if (mainBitmap != null) {
                    mainBitmap.recycle();
                    mainBitmap = null;
                    System.gc();
                }
                mainBitmap = bmp;
                mainImage.setImageBitmap(mainBitmap);
                float scale = (float) bmp.getWidth() / (float) bmp.getHeight();
                ImageScaleType scaleType;
                if (scale <= (3f / 4f)) {
                    scaleType = ImageScaleType.higher;
                } else if (scale >= (4f / 3f)) {
                    scaleType = ImageScaleType.wider;
                } else {
                    scaleType = ImageScaleType.Wrap;
                    scaleType.scale = scale;
                }
                imageEditContainer.setScaleType(scaleType);
                mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            }


        });

    }

    /**
     * 按下返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (mode) {
                case MODE_STICKERS:
                    mStirckerFragment.backToMain();
                    return true;
                case MODE_FILTER:// 滤镜编辑状态
                    mFliterListFragment.backToMain();// 保存滤镜贴图
                    return true;
                case MODE_CROP:// 剪切图片保存
                    mCropFragment.backToMain();
                    return true;
                case MODE_ROTATE:// 旋转图片保存
                    mRotateFragment.backToMain();
                    return true;
            }// end switch

            forceReturnBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 强制推出
     */
    private void forceReturnBack() {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    /**
     * 切换底图Bitmap
     *
     * @param newBit
     */
    public void changeMainBitmap(Bitmap newBit) {
        if (mainBitmap != null) {
            if (!mainBitmap.isRecycled()) {// 回收
                mainBitmap.recycle();
            }
            mainBitmap = newBit;
        } else {
            mainBitmap = newBit;
        }// end if
        mainImage.setImageBitmap(mainBitmap);
    }


    public void setViewPageHeight(int mode) {
        ViewGroup.LayoutParams layoutParams = menuFrameLayout.getLayoutParams();
        if (mode == MODE_FILTER) {
            layoutParams.height = ScreenUtils.dp2px(120, this);
            menuFrameLayout.setLayoutParams(layoutParams);
            mFliterListFragment.refreshAdapter();
        } else {
            layoutParams.height = ScreenUtils.dp2px(65, this);
            menuFrameLayout.setLayoutParams(layoutParams);
        }
    }


    public void setCurrentItem(int index) {
        setViewPageHeight(index);
        Fragment nextFragment;
        if (index == 0)
            nextFragment = mMainMenuFragment;// 主菜单
        else if (index == 1)
            nextFragment = mStirckerFragment;// 贴图
        else if (index == 2)
            nextFragment = mFliterListFragment;// 滤镜
        else if (index == 3)
            nextFragment = mCropFragment;// 剪裁
        else if (index == 4)
            nextFragment = mRotateFragment;// 旋转
        else
            nextFragment = mMainMenuFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (nextFragment.isAdded()) {
            ft.show(nextFragment);
        } else {
            ft.add(R.id.bottom_menu, nextFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
        currentFragment = nextFragment;
    }


    public interface OnSaveImageCallBack {
        void onSaved(Bitmap resultBmp);
    }

    /**
     * 保存按钮点击
     */
    private final class ApplyBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (mode) {
                case MODE_STICKERS:
                    mStirckerFragment.saveStickers();// 保存贴图
                    break;
                case MODE_FILTER:// 滤镜编辑状态
                    mFliterListFragment.saveFilterImage();// 保存滤镜贴图
                    break;
                case MODE_CROP:// 剪切图片保存
                    mCropFragment.saveCropImage();
                    break;
                case MODE_ROTATE:// 旋转图片保存
                    mRotateFragment.saveRotateImage();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 保存按钮 点击退出
     */
    private final class SaveBtnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("save_file_path", saveFilePath);
            mContext.setResult(RESULT_OK, returnIntent);
            mContext.finish();
        }
    }
}
