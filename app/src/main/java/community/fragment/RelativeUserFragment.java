
package community.fragment;

import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.HttpProtocol;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.imagepicker.util.BroadcastUtils;
import com.umeng.comm.ui.imagepicker.widgets.RefreshLayout;
import com.umeng.comm.ui.presenter.impl.ActiveUserFgPresenter;
import com.umeng.comm.ui.presenter.impl.RelativeUserFgPresenter;

import java.util.List;

/**
 * 相关用户Fragment
 */
public class RelativeUserFragment extends RecommendUserFragment {

    private View mBackView = null;
    private BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
        public void onReceiveUser(android.content.Intent intent) {
            CommUser newUser = getUser(intent);
            BroadcastUtils.BROADCAST_TYPE type = getType(intent);
            boolean follow = true;
            if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_FOLLOW) {
                follow = true;
            } else if (type == BroadcastUtils.BROADCAST_TYPE.TYPE_USER_CANCEL_FOLLOW) {
                follow = false;
            }
            List<CommUser> users = mAdapter.getDataSource();
            for (CommUser user : users) {
                if (user.id.equals(newUser.id)) {
                    user.extraData.putBoolean(Constants.IS_FOCUSED, follow);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void initWidgets() {
        super.initWidgets();
        List<CommUser> users = getArguments().getParcelableArrayList(Constants.TAG_USERS);
        mAdapter.addData(users);
        mRefreshLvLayout.setRefreshing(true);
        String nextPageUrl = getArguments().getString(HttpProtocol.NAVIGATOR_KEY);
        mPresenter.setNextPageUrl(nextPageUrl);
        mRootView.findViewById(ResFinder.getId("umeng_comm_save_bt")).setVisibility(View.GONE);
        mBackView = mRootView.findViewById(ResFinder.getId("umeng_comm_setting_back"));
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(this);
        mTitleTextView.setText(ResFinder.getString("umeng_comm_relation_user"));
        mRefreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                mPresenter.loadMoreData();
            }
        });
        BroadcastUtils.registerUserBroadcast(getActivity(), mReceiver);
    }

    @Override
    protected ActiveUserFgPresenter createPresenters() {
        return new RelativeUserFgPresenter(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackView) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        BroadcastUtils.unRegisterBroadcast(getActivity(), mReceiver);
        super.onDestroy();
    }

}
