package editimage.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uy.imageeditlibrary.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import editimage.EditImageActivity;
import editimage.adapter.FliterAdapter;
import editimage.fliter.FliterType;
import editimage.fliter.PhotoProcessing;
import helper.common_util.ScreenUtils;


/**
 * 滤镜列表fragment
 */
public class FliterListFragment extends Fragment {
    public static final String TAG = FliterListFragment.class.getName();
    private View viewGroup;

    private Bitmap fliterBit;// 滤镜处理后的bitmap
    private EditImageActivity activity;

    private Bitmap currentBitmap;// 标记变量
    private RecyclerView hlistView;
    private FliterAdapter adapter;
    private FliterItemListener fliterItemListener = new FliterItemListener() {
        @Override
        public void onClickItem(FliterType type) {
            int position = type.ordinal();
            if (position == 0) {// 原始图片效果
                currentBitmap = activity.preBitmap;
                activity.mainImage.setImageBitmap(currentBitmap);
                return;
            }
            // 滤镜处理
            ProcessingImage task = new ProcessingImage();
            task.execute(position);
        }
    };

    public static FliterListFragment newInstance(EditImageActivity activity) {
        FliterListFragment fragment = new FliterListFragment();
        fragment.activity = activity;
        return fragment;
    }

    /**
     * 保存Bitmap图片到指定文件
     */
    public static boolean saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // System.out.println("保存文件--->" + f.getAbsolutePath());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewGroup = inflater.inflate(R.layout.fragment_edit_image_fliter, null);
        hlistView = (RecyclerView) viewGroup.findViewById(R.id.listview);
        return viewGroup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpFliters();
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        currentBitmap = activity.mainBitmap;
        fliterBit = null;
        activity.mainImage.setImageBitmap(activity.mainBitmap);// 返回原图
        activity.currentMode = EditImageActivity.MODE_NONE;
        activity.setCurrentItem(0);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();
    }

    /**
     * 保存滤镜处理后的图片
     */
    public void saveFilterImage() {
        if (currentBitmap == activity.mainBitmap) {// 原始图片
            backToMain();
            return;
        } else {
            if (activity.mainBitmap != null
                    && !activity.mainBitmap.isRecycled()) {
                activity.mainBitmap.recycle();
            }
            activity.mainBitmap = fliterBit;
            backToMain();
        }
    }

    /**
     * 装载滤镜
     */
    private void setUpFliters() {
        List<FliterType> fliterTypes = new ArrayList<>();
        for (FliterType type : FliterType.FILTER_TYPES) {
            fliterTypes.add(type);
        }
        adapter = new FliterAdapter(fliterTypes, fliterItemListener);
        hlistView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hlistView.addItemDecoration(new FliterItemDecoration());
        hlistView.setAdapter(adapter);
    }

    public void refreshAdapter() {
        if (adapter == null)
            return;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (fliterBit != null && (!fliterBit.isRecycled())) {
            fliterBit.recycle();
        }
        super.onDestroy();
    }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }

    public interface FliterItemListener {
        void onClickItem(FliterType type);
    }

    /**
     * 图片滤镜处理任务
     */
    private final class ProcessingImage extends
            AsyncTask<Integer, Void, Bitmap> {
        private Dialog dialog;
        private Bitmap srcBitmap;

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int type = params[0];
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }

            srcBitmap = Bitmap.createBitmap(activity.mainBitmap.copy(
                    Bitmap.Config.RGB_565, true));
            return PhotoProcessing.filterPhoto(srcBitmap, type);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            activity.progressBar.setVisibility(View.GONE);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            activity.progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result == null)
                return;
            if (fliterBit != null && (!fliterBit.isRecycled())) {
                fliterBit.recycle();
            }
            fliterBit = result;
            activity.mainImage.setImageBitmap(fliterBit);
            currentBitmap = fliterBit;
            activity.progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.progressBar.setVisibility(View.VISIBLE);
        }
    }

    public class FliterItemDecoration extends RecyclerView.ItemDecoration {
        int offset;

        public FliterItemDecoration() {
            offset = ScreenUtils.dp2px(5, getContext());
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(offset, offset, offset, offset);
        }
    }
}
