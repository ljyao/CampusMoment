package community.adapter;

import android.view.ViewGroup;

import com.umeng.comm.core.beans.CommUser;

import adapter.RecycleAdapter;
import community.fragment.FollowedUserFragment;
import community.views.UserView;
import community.views.UserView_;

public class UserAdapter extends RecycleAdapter<CommUser, UserView> {

    private FollowedUserFragment.OnClickFollowListener followListener;

    @Override
    protected UserView onCreateItemView(ViewGroup parent, int viewType) {
        UserView userView = UserView_.build(parent.getContext(), null);
        userView.setFollowListener(followListener);
        return userView;
    }

    public void setFollowListener(FollowedUserFragment.OnClickFollowListener followListener) {
        this.followListener = followListener;
    }
}
