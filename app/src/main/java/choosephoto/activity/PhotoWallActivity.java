package choosephoto.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uy.bbs.R;
import com.uy.util.BitmapUtils;
import com.uy.util.ScreenUtils;

import java.io.File;
import java.util.ArrayList;

import choosephoto.adapter.Decoration;
import choosephoto.adapter.PhotoWallAdapter;
import editimage.EditImageActivity;
import helper.AppConstants;
import helper.util.FileUtils;


/**
 * 选择照片页面
 */
public class PhotoWallActivity extends AppCompatActivity {

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
    /**
     * 当前展示的是否为最近照片
     */
    private boolean isLatest = true;
    /**
     * 第一次跳转至相册页面时，传递最新照片信息
     */
    private boolean firstIn = true;
    private int requestCode;

    public static void destroy() {
        if (mContext != null) {
            Activity context = mContext;
            mContext = null;
            context.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_wall);
        mContext = this;

        requestCode = getIntent().getIntExtra("from", -1);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.latest_image));
        Intent intent = getIntent();
        boolean singleChoose = intent.getBooleanExtra("SingleChoose", false);
        mPhotoWall = (RecyclerView) findViewById(R.id.photo_wall_grid);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mPhotoWall.setLayoutManager(layoutManager);
        mPhotoWall.setItemAnimator(new DefaultItemAnimator());
        mPhotoWall.addItemDecoration(new Decoration(ScreenUtils.dp2px(1, PhotoWallActivity.this)));
        list = getLatestImagePaths(100);
        adapter = new PhotoWallAdapter(this, list, singleChoose);
        mPhotoWall.setAdapter(adapter);

        confirmPhoto = (TextView) findViewById(R.id.confirm_photo);
        confirmPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> paths = getSelectImagePaths();
                if (paths == null || paths.size() == 0) {
                    return;
                }
                String path = paths.get(0);
                if (requestCode == AppConstants.REQUEST_EDIT_USERHEAD) {
                    Intent i = new Intent();
                    i.setData(Uri.parse(path));
                    editResult(i);
                } else {
                    Uri uri = Uri.parse("file://" + path);
                    Intent i = new Intent(PhotoWallActivity.this, EditImageActivity.class);
                    i.putExtra(EditImageActivity.FILE_PATH, path);
                    String outPath = FileUtils.getCacheDir().getAbsolutePath() + "/tmp"
                            + System.currentTimeMillis() + ".jpg";
                    i.putExtra(EditImageActivity.EXTRA_OUTPUT, outPath);
                    startActivityForResult(i, AppConstants.REQUEST_PHOTO_FEED);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backAction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void editResult(Intent data) {
        Uri uri = data.getData();
        Intent intent = new Intent();
        intent.setData(uri);
        PhotoAlbumActivity.destroy();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            editResult(data);
        }
    }

    /**
     * 点击返回时，跳转至相册页面
     */
    private void backAction() {
        Intent intent = new Intent(this, PhotoAlbumActivity.class);
        // 传递“最近照片”分类信息
        if (firstIn) {
            if (list != null && list.size() > 0) {
                intent.putExtra("latest_count", list.size());
                intent.putExtra("latest_first_img", list.get(0));
            }
            firstIn = false;
        }
        startActivity(intent);
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

    /**
     * 根据图片所属文件夹路径，刷新页面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();

        if (code == 100) { // 某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            setTitle(folderName);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) { // 最近照片
            setTitle(R.string.latest_image);
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

        ContentResolver mContentResolver = getContentResolver();

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
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int code = intent.getIntExtra("code", -1);
        if (code == 100) {
            // 某个相册
            String folderPath = intent.getStringExtra("folderPath");
            if (isLatest
                    || (folderPath != null && !folderPath.equals(currentFolder))) {
                currentFolder = folderPath;
                updateView(100, currentFolder);
                isLatest = false;
            }
        } else if (code == 200) {
            // “最近照片”
            if (!isLatest) {
                updateView(200, null);
                isLatest = true;
            }
        }
    }
}
