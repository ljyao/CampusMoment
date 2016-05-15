package community.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.uy.bbs.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import community.activity.FeedListActivity_;
import community.providable.FeedPrvdr;


/**
 * Created by ljy on 15/12/25.
 */
@EFragment(R.layout.message_fragment)
public class MessageFragment extends Fragment {
    @Click(R.id.comment_rl)
    public void onclickComment() {
        FeedListActivity_.intent(this).title("我的评论")
                .feedType(FeedPrvdr.FeedType.ReceivedComments).start();
    }

    @Click(R.id.at_me_rl)
    public void onclickAtMe() {
        FeedListActivity_.intent(this).title("@我的")
                .feedType(FeedPrvdr.FeedType.AtFeeds).start();
    }

    @Click(R.id.like_me_rl)
    public void onclickLikeMe() {
        FeedListActivity_.intent(this).title("赞我的")
                .feedType(FeedPrvdr.FeedType.LikedMe).start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}
