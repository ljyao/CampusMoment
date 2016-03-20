package community.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.uy.bbs.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import community.activity.CommentReceivedActivity_;

/**
 * Created by ljy on 15/12/25.
 */
@EFragment(R.layout.message_fragment)
public class MessageFragment extends Fragment {
    @Click(R.id.comment_rl)
    public void onclickComment() {
        CommentReceivedActivity_.intent(this).start();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}
