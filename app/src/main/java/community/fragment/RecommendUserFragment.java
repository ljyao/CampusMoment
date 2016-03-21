
package community.fragment;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.comm.core.listeners.Listeners.OnResultListener;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.imagepicker.widgets.RefreshLayout;
import com.umeng.comm.ui.presenter.impl.ActiveUserFgPresenter;
import com.umeng.comm.ui.presenter.impl.RecommendUserFgPresenter;
import com.uy.bbs.R;


/**
 * 用户推荐页面
 */
public class RecommendUserFragment extends ActiveUserFragment implements OnClickListener {

    protected TextView mTitleTextView;
    private boolean mSaveButtonVisiable = true;
    private ViewStub mViewStub;
    private View mEmptyView;
    /**
     * 默认逻辑。点击跳过时销毁该Activity
     */
    private OnResultListener mResultListener = new OnResultListener() {

        @Override
        public void onResult(int status) {
            getActivity().finish();
        }
    };

    @Override
    protected int getFragmentLayout() {
        return R.layout.recommend_user_layout;
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        Button button = (Button) mRootView.findViewById(ResFinder.getId("umeng_comm_save_bt"));
        button.setOnClickListener(this);
        button.setText(ResFinder.getString("umeng_comm_skip"));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        button.setTextColor(ResFinder.getColor("umeng_comm_skip_text_color"));
        if (!mSaveButtonVisiable) {
            button.setVisibility(View.GONE);
            mRootView.findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(
                    this);
            mAdapter.setFromFindPage(!mSaveButtonVisiable);
        } else {
            mRootView.findViewById(ResFinder.getId("umeng_comm_setting_back")).setVisibility(
                    View.GONE);
        }
        mTitleTextView = (TextView) mRootView.findViewById(ResFinder
                .getId("umeng_comm_setting_title"));
        mTitleTextView.setText(ResFinder.getString("umeng_comm_recommend_user"));
        mTitleTextView.setTextColor(Color.BLACK);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        mRootView.findViewById(ResFinder.getId("umeng_comm_title_bar_root"))
                .setBackgroundColor(Color.WHITE);

        mRefreshLvLayout.setEnabled(true);
        mViewStub = (ViewStub) mRootView.findViewById(ResFinder.getId("umeng_comm_empty"));

        mRefreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                mPresenter.loadMoreData();
            }
        });

        mRefreshLvLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.loadDataFromServer();
            }
        });
    }

    @Override
    protected ActiveUserFgPresenter createPresenters() {
        return new RecommendUserFgPresenter(this);
    }

    @Override
    public void showEmptyView() {
        mEmptyView = mViewStub.inflate();
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        if (mEmptyView != null && mEmptyView.getVisibility() == View.VISIBLE) {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResFinder.getId("umeng_comm_save_bt")
                || id == ResFinder.getId("umeng_comm_setting_back")) { // 跳过事件
            mResultListener.onResult(0);
        }
    }

    /**
     * 设置跳过按钮不可见。在设置页面显示推荐用户的时候不需要显示。</br>
     */
    public void setSaveButtonInvisiable() {
        mSaveButtonVisiable = false;
    }

    /**
     * 设置点击跳过时得回调</br>
     *
     * @param listener
     */
    public void setOnResultListener(OnResultListener listener) {
        mResultListener = listener;
    }

}
