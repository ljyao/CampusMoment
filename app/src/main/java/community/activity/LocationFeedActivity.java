
package community.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.uy.bbs.R;

import community.fragment.FeedFragment;
import community.fragment.FeedFragment_;
import community.providable.FeedPrvdr;

/**
 * 某个地理位置的Feed
 */
public class LocationFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.location_feed_activity);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FeedItem feedItem = getIntent().getParcelableExtra(Constants.FEED);
        setTitle(feedItem.locationAddr);

        FeedFragment fragment = FeedFragment_.builder().build();
        fragment.setFeedType(FeedPrvdr.FeedType.LocationFeed, feedItem.location);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.main_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

}
