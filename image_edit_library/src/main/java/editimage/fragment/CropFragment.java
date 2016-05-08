package editimage.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uy.imageeditlibrary.R;

import java.util.ArrayList;
import java.util.List;

import editimage.EditImageActivity;
import editimage.model.ImageScaleType;
import editimage.model.RatioItem;
import editimage.view.CropImageView;


/**
 * 图片剪裁Fragment
 */
public class CropFragment extends Fragment {
    public static final String TAG = CropFragment.class.getName();
    public static int SELECTED_COLOR = Color.BLUE;
    public static int UNSELECTED_COLOR = Color.BLACK;
    private static List<RatioItem> dataList = new ArrayList<>();

    static {
        dataList.add(new RatioItem(ImageScaleType.wider));
        dataList.add(new RatioItem(ImageScaleType.square));
        dataList.add(new RatioItem(ImageScaleType.higher));
    }

    public CropImageView mCropPanel;// 剪裁操作面板
    public TextView selctedTextView;
    private View mainView;
    private EditImageActivity activity;
    private View backToMenu;// 返回主菜单
    private LinearLayout ratioList;
    private List<TextView> textViewList = new ArrayList<>();
    private CropRationClick mCropRationClick = new CropRationClick();

    public static CropFragment newInstance(EditImageActivity activity) {
        CropFragment fragment = new CropFragment();
        fragment.activity = activity;
        fragment.mCropPanel = activity.mCropPanel;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        ratioList = (LinearLayout) mainView.findViewById(R.id.ratio_list_group);
        setUpRatioList();
        return mainView;
    }

    private void setUpRatioList() {
        // init UI
        ratioList.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = 20;
        params.rightMargin = 20;
        for (int i = 0, len = dataList.size(); i < len; i++) {
            TextView text = new TextView(activity);
            text.setTextColor(UNSELECTED_COLOR);
            text.setTextSize(20);
            text.setText(dataList.get(i).getText());
            textViewList.add(text);
            ratioList.addView(text, params);
            text.setTag(i);
            if (i == 0) {
                selctedTextView = text;
            }
            dataList.get(i).setIndex(i);
            text.setTag(dataList.get(i));
            text.setOnClickListener(mCropRationClick);
        }
        selctedTextView.setTextColor(SELECTED_COLOR);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        activity.currentMode = EditImageActivity.MODE_NONE;
        mCropPanel.setVisibility(View.GONE);
        activity.mainImage.setScaleEnabled(true);// 恢复缩放功能
        activity.setCurrentItem(0);
        if (selctedTextView != null) {
            selctedTextView.setTextColor(UNSELECTED_COLOR);
        }
        mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(), -1);
        activity.bannerFlipper.showPrevious();
        activity.mStickerView.setVisibility(View.VISIBLE);
    }

    public void saveCropImage() {
        backToMain();
    }


    private final class CropRationClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            TextView curTextView = (TextView) v;
            selctedTextView.setTextColor(UNSELECTED_COLOR);
            RatioItem dataItem = (RatioItem) v.getTag();
            selctedTextView = curTextView;
            selctedTextView.setTextColor(SELECTED_COLOR);
            activity.setImageScaleType(dataItem.getScaleType());
        }
    }// end inner class

    /**
     * 返回按钮逻辑
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }// end class

}
