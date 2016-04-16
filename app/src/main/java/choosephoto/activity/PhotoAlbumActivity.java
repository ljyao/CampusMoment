package choosephoto.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.uy.bbs.R;

import java.util.ArrayList;

import choosephoto.adapter.PhotoAlbumLVAdapter;
import choosephoto.adapter.PhotoAlbumLVItem;
import choosephoto.util.ImageLoader;
import helper.common_util.FileUtils;

/**
 * 分相册查看SD卡所有图片。
 */
public class PhotoAlbumActivity extends AppCompatActivity {
    private static Activity mContext;
    private ArrayList<PhotoAlbumLVItem> list = new ArrayList<>();
    private PhotoAlbumLVAdapter adapter;

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
        ArrayList<PhotoAlbumLVItem> fileLists = ImageLoader.getImagePaths(this);
        list.addAll(fileLists);
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
        ImageLoader.getInstance(mContext).destroy();
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

}
