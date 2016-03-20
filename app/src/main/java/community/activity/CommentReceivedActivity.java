package community.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.providable.FeedPrvdr;

/**
 * Created by Shine on 2016/3/16.
 */
@EActivity(R.layout.activity_comment_received)
public class CommentReceivedActivity extends AppCompatActivity {
    private FeedFragment fragment;

    @AfterViews
    public void initView() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("收的评论");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = FeedFragment_.builder().feedType(FeedPrvdr.FeedType.ReceivedComments).build();
        ft.add(R.id.fragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }
}
