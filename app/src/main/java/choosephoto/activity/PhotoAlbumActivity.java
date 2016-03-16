package choosephoto.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.uy.bbs.R;
import com.uy.util.Worker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import choosephoto.adapter.PhotoAlbumLVAdapter;
import choosephoto.adapter.PhotoAlbumLVItem;
import helper.common_util.FileUtils;

/**
 * 分相册查看SD卡所有图片。
 */
public class PhotoAlbumActivity extends AppCompatActivity {
    private static Activity mContext;
    private static ArrayList<PhotoAlbumLVItem> fileLists = new ArrayList<>();
    private ArrayList<PhotoAlbumLVItem> list = new ArrayList<>();
    private PhotoAlbumLVAdapter adapter;

    public static void destroy() {
        if (mContext != null) {
            Activity context = mContext;
            mContext = null;
            context.finish();
        }
    }

    public static void initData(final Context context, final LoadFileCallBack callBack) {
        synchronized (fileLists) {
            Worker.postThread(new Runnable() {
                @Override
                public void run() {
                    // 相册
                    fileLists = getImagePaths(context);
                    Worker.postMain(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onLoad();
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取目录中图片的个数。
     */
    private static int getImageCount(File folder) {
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
    private static String getFirstImagePath(File folder) {
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
    private static ArrayList<PhotoAlbumLVItem> getImagePaths(Context context) {
        final ArrayList<PhotoAlbumLVItem> albumLVItems = new ArrayList<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();

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

                    final File parentFile = new File(imagePath).getParentFile();
                    final String parentPath = parentFile.getAbsolutePath();

                    // 不扫描重复路径
                    if (!cachePath.contains(parentPath)) {
                        albumLVItems.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile), getFirstImagePath(parentFile)));
                        cachePath.add(parentPath);
                    }

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }

            cursor.close();
        }

        return albumLVItems;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        mContext = this;

        if (!FileUtils.isStorageOK()) {
            Toast.makeText(this, "SD卡不可用!", Toast.LENGTH_LONG).show();
            return;
        }

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.select_album));

        ListView listView = (ListView) findViewById(R.id.select_img_listView);


        if (!getIntent().hasExtra("latest_count")) {
            return;
        }
        // “最近照片”
        Intent t = getIntent();
        list.add(new PhotoAlbumLVItem(getResources().getString(R.string.latest_image), t.getIntExtra("latest_count", -1), t.getStringExtra("latest_first_img")));
        if (fileLists != null && fileLists.size() > 0) {
            list.addAll(fileLists);
        } else {
            initData(this, new LoadFileCallBack() {
                @Override
                public void onLoad() {
                    list.addAll(fileLists);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        adapter = new PhotoAlbumLVAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
                // 第一行为“最近照片”
                if (position == 0) {
                    intent.putExtra("code", 200);
                } else {
                    intent.putExtra("code", 100);
                    intent.putExtra("folderPath", list.get(position).getPathName());
                }
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backAction();
            return true;
        }
        return false;
    }

    private void backAction() {
        PhotoWallActivity.destroy();
        finish();
    }

    // 重写返回键
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public interface LoadFileCallBack {
        void onLoad();
    }
}
