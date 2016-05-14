
package choosephoto.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import choosephoto.views.PhotoPreview;
import choosephoto.views.PhotoPreview_;

@EActivity(R.layout.activity_photopreview)
public class PhotoPreviewActivity extends AppCompatActivity implements OnPageChangeListener {
    @ViewById(R.id.viewpager)
    public ViewPager mViewPager;
    @Extra
    public String title = "";
    @Extra
    protected ArrayList<String> photos;
    @Extra
    protected int current = 0;
    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return photos == null ? 0 : photos.size();
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoPreview photoPreview = PhotoPreview_.build(getApplicationContext(), null);
            container.addView(photoPreview);
            photoPreview.loadImage(photos.get(position));
            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };

    @AfterViews
    public void initViews() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        current = arg0;
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
