package choosephoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.uy.bbs.R;

import java.util.ArrayList;

import choosephoto.fragment.PhotoAlbumFragment;
import choosephoto.fragment.PhotoWallFragment;

/**
 * Created by shine on 16-2-24.
 */
public class ChoosePhotoActivity extends AppCompatActivity {
    private static final int MODE_PHOTOALBUM = 0;
    private static final int MODE_PHOTOWALL = 1;
    private PhotoAlbumFragment photoAlbumFragment;
    private PhotoWallFragment photoWallFragment;
    private FrameLayout frameLayout;
    private int mode;
    private ChoosePhotoListener listener = new ChoosePhotoListener() {
        @Override
        public void onClickPhotoAlbum(String folderPath) {

        }

        @Override
        public void onClickConfirmPhoto(ArrayList<String> paths) {

        }

        @Override
        public void onCancel() {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.select_album));

        Intent intent = getIntent();
        boolean singleChoose = intent.getBooleanExtra("SingleChoose", false);

        frameLayout = (FrameLayout) findViewById(R.id.fragment);

        photoAlbumFragment = new PhotoAlbumFragment();
        photoAlbumFragment.setChoosePhotoListener(listener);
        photoWallFragment = new PhotoWallFragment();
        photoWallFragment.setChoosePhotoListener(listener);
        showFragment(MODE_PHOTOWALL);
    }

    public void showFragment(int i) {
        mode = i;
        Fragment nextFragment, currentFragment;
        if (i == MODE_PHOTOALBUM) {
            nextFragment = photoAlbumFragment;
            currentFragment = photoWallFragment;
        } else {
            nextFragment = photoWallFragment;
            currentFragment = photoAlbumFragment;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (nextFragment.isAdded()) {
            ft.show(nextFragment);
        } else {
            ft.add(R.id.fragment, nextFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mode == MODE_PHOTOWALL) {
                showFragment(MODE_PHOTOALBUM);
            } else {

            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public interface ChoosePhotoListener {
        void onClickPhotoAlbum(String folderPath);

        void onClickConfirmPhoto(ArrayList<String> paths);

        void onCancel();
    }
}
