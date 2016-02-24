package choosephoto.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uy.bbs.R;
import com.uy.util.BitmapUtils;

import java.io.File;
import java.util.ArrayList;

import choosephoto.ChoosePhotoActivity;
import choosephoto.adapter.Decoration;
import choosephoto.adapter.PhotoWallAdapter;
import helper.common_util.FileUtils;
import helper.common_util.ScreenUtils;

/**
 * Created by shine on 16-2-24.
 */
public class PhotoWallFragment extends Fragment {
    private static final int EDIT_PHOTO = 0;
    private static Activity mContext;
    private ArrayList<String> list;
    private RecyclerView mPhotoWall;
    private PhotoWallAdapter adapter;
    private TextView confirmPhoto;
    /**
     * 当前文件夹路径
     */
    private String currentFolder = null;

    private int requestCode;
    private ChoosePhotoActivity.ChoosePhotoListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewGroup = inflater.inflate(R.layout.activity_photo_wall, null);
        mPhotoWall = (RecyclerView) viewGroup.findViewById(R.id.photo_wall_grid);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mPhotoWall.setLayoutManager(layoutManager);
        mPhotoWall.setItemAnimator(new DefaultItemAnimator());
        mPhotoWall.addItemDecoration(new Decoration(ScreenUtils.dp2px(1, getContext())));
        list = getLatestImagePaths(100);
        adapter = new PhotoWallAdapter(getContext(), list, false);
        mPhotoWall.setAdapter(adapter);

        confirmPhoto = (TextView) viewGroup.findViewById(R.id.confirm_photo);
        confirmPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> paths = getSelectImagePaths();
                listener.onClickConfirmPhoto(paths);
            }
        });
        return viewGroup;
    }


    /**
     * 根据图片所属文件夹路径，刷新页面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();

        if (code == 100) { // 某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            getActivity().setTitle(folderName);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) { // 最近照片
            getActivity().setTitle(R.string.latest_image);
            list.addAll(getLatestImagePaths(100));
        }

        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            // 滚动至顶部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath) {
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return null;
        }
        ArrayList<String> imageFilePaths = new ArrayList<String>();
        for (int i = allFileNames.length - 1; i >= 0; i--) {
            String filePath = folderPath + File.separator + allFileNames[i];
            if (FileUtils.isImage(allFileNames[i], filePath)) {
                imageFilePaths.add(filePath);
            }
        }

        return imageFilePaths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContext().getContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri,
                new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                        + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            // 从最新的图片开始读取.
            // 当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    if (BitmapUtils.isImage(path)) {
                        latestImagePaths.add(path);
                    }

                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return latestImagePaths;
    }

    // 获取已选择的图片路径
    private ArrayList<String> getSelectImagePaths() {
        SparseBooleanArray map = adapter.getSelectionMap();
        if (map.size() == 0) {
            return null;
        }

        ArrayList<String> selectedImageList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            if (map.get(i)) {
                selectedImageList.add(list.get(i));
            }
        }

        return selectedImageList;
    }

    // 从相册页面跳转至此页
    public void setData(String path) {
        if (TextUtils.isEmpty(path)) {
            updateView(200, null);
        } else {
            currentFolder = path;
            updateView(100, currentFolder);
        }
    }

    public void setChoosePhotoListener(ChoosePhotoActivity.ChoosePhotoListener listener) {
        this.listener = listener;
    }
}
