package community.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import community.fragment.TopicFragment;


/**
 * Created by Shine on 2016/3/6.
 */
@EActivity(R.layout.activity_hot_topic)
public class HotTopicActivity extends AppCompatActivity {
    private TopicFragment fragment;

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("热门话题");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new TopicFragment();
        ft.add(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }
}
