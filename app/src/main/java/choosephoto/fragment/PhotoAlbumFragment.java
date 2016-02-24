package choosephoto.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chat.util.PhotoAlbumLVItem;
import com.uy.bbs.R;
import com.uy.util.Worker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import choosephoto.ChoosePhotoActivity;
import choosephoto.adapter.PhotoAlbumLVAdapter;
import helper.common_util.FileUtils;

/**
 * Created by shine on 16-2-24.
 */
public class PhotoAlbumFragment extends Fragment {
    private ArrayList<PhotoAlbumLVItem> list = new ArrayList<>();
    private PhotoAlbumLVAdapter adapter;
    private ListView listView;
    private ChoosePhotoActivity.ChoosePhotoListener listener;

    public void setChoosePhotoListener(ChoosePhotoActivity.ChoosePhotoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewGroup = inflater.inflate(R.layout.activity_photo_album, null);
        listView = (ListView) viewGroup.findViewById(R.id.select_img_listView);
        return viewGroup;
    }

    public void setData(Intent intent) {
        if (!intent.hasExtra("latest_count")) {
            return;
        }
        // “最近照片”
        int fileCount = intent.getIntExtra("latest_count", -1);
        String lastImage = getResources().getString(R.string.latest_image);
        String firstPhotoPath = intent.getStringExtra("latest_first_img");
        PhotoAlbumLVItem albumItem = new PhotoAlbumLVItem(lastImage, fileCount, firstPhotoPath);
        list.add(albumItem);
        initData();
        adapter = new PhotoAlbumLVAdapter(getContext(), list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String folderPath = null;
                if (position != 0)
                    folderPath = list.get(position).getPathName();
                listener.onClickPhotoAlbum(folderPath);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void initData() {

        Worker.postExecuteTask(new Runnable() {
            @Override
            public void run() {
                // 相册
                getImagePathsByContentProvider();

            }
        });
    }

    /**
     * 获取目录中图片的个数。
     */
    private int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            if (FileUtils.isImage(file.getName(), file.getAbsolutePath())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            if (FileUtils.isImage(file.getName(), file.getAbsolutePath())) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContext().getContentResolver();

        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri,
                new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                        + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        if (cursor != null) {
            if (cursor.moveToLast()) {
                // 路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<>();

                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);

                    File parentFile = new File(imagePath).getParentFile();
                    String parentPath = parentFile.getAbsolutePath();

                    // 不扫描重复路径
                    if (!cachePath.contains(parentPath)) {
                        this.list.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile), getFirstImagePath(parentFile)));
                        Worker.postMain(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        cachePath.add(parentPath);
                    }

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }

            cursor.close();
        }

        return list;
    }

}
