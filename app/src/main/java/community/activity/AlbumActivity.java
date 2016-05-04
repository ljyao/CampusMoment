package community.activity;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import community.fragment.AlbumsFragment;
import community.fragment.AlbumsFragment_;
import editimage.BaseActivity;

/**
 * 用户相册Activity
 */
@EActivity(R.layout.user_album_layout)
public class AlbumActivity extends BaseActivity {
    @Extra
    public CommUser user;

    @AfterViews
    protected void initViews() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("我的相册");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        AlbumsFragment albumsFragment = AlbumsFragment_.builder().user(user).build();
        transaction.replace(R.id.user_album_fragment, albumsFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
