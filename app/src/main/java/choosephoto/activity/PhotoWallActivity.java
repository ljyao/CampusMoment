package choosephoto.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uy.bbs.R;

import java.io.File;
import java.util.ArrayList;

import choosephoto.adapter.Decoration;
import choosephoto.adapter.PhotoWallAdapter;
import choosephoto.util.ImageLoader;
import editimage.EditImageActivity;
import helper.common_util.FileUtils;
import helper.common_util.ScreenUtils;
import model.AppConstants;


/**
 * 选择照片页面
 */
public class PhotoWallActivity extends AppCompatActivity {

    private static final int EDIT_PHOTO = 0;
    private static Activity mContext;
    protected ArrayList<String> imagesSelected;
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
    private TextView preViewBtN;

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
        imagesSelected = (ArrayList<String>) intent.getSerializableExtra("imagesSelected");
        mPhotoWall = (RecyclerView) findViewById(R.id.photo_wall_grid);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mPhotoWall.setLayoutManager(layoutManager);
        mPhotoWall.setItemAnimator(new DefaultItemAnimator());
        mPhotoWall.addItemDecoration(new Decoration(ScreenUtils.dp2px(1, PhotoWallActivity.this)));
        list = ImageLoader.getLatestImagePaths(this, 100);
        adapter = new PhotoWallAdapter(this, list, singleChoose);
        adapter.addImagesSelected(imagesSelected);
        mPhotoWall.setAdapter(adapter);
        preViewBtN = (TextView) findViewById(R.id.preview_btn);
        preViewBtN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> preViewPhotos = new ArrayList<>();
                ArrayList<String> selectPhotos = adapter.getSelectImagePaths();
                for (String path : selectPhotos) {
                    preViewPhotos.add("file:///" + path);
                }
                PhotoPreviewActivity_.intent(PhotoWallActivity.this).photos(preViewPhotos).start();
            }
        });
        confirmPhoto = (TextView) findViewById(R.id.confirm_photo);
        confirmPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> paths = adapter.getSelectImagePaths();
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
                    String outPath = FileUtils.getCacheDir(PhotoWallActivity.this) + "/tmp"
                            + System.currentTimeMillis() + ".jpg";
                    i.putExtra(EditImageActivity.EXTRA_OUTPUT, outPath);
                    startActivityForResult(i, AppConstants.REQUEST_PHOTO_FEED);
                    ImageLoader.getInstance(mContext).destroy();
                    PhotoAlbumActivity.destroy();
                    finish();
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
        adapter.getSelectImagePaths();
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
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
        ArrayList<String> list = new ArrayList<>();
        if (code == 100) { // 某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            setTitle(folderName);
            list.addAll(ImageLoader.getAllImagePathsByFolder(folderPath));
        } else if (code == 200) { // 最近照片
            setTitle(R.string.latest_image);
            list.addAll(ImageLoader.getLatestImagePaths(this, 100));
        }
        adapter.updateData(list);
        if (list.size() > 0) {
            // 滚动至顶部
            mPhotoWall.smoothScrollToPosition(0);
        }
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
