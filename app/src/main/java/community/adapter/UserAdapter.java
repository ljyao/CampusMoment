package community.adapter;

import android.view.ViewGroup;

import com.umeng.comm.core.beans.CommUser;

import adapter.RecycleAdapter;
import community.views.UserView;
import community.views.UserView_;

public class UserAdapter extends RecycleAdapter<CommUser, UserView> {

    @Override
    protected UserView onCreateItemView(ViewGroup parent, int viewType) {
        return UserView_.build(parent.getContext(), null);
    }
}
