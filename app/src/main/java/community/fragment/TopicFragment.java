
package community.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.ui.fragments.RecommendTopicFragment;
import com.umeng.comm.ui.presenter.impl.RecommendTopicPresenter;
import com.umeng.comm.ui.presenter.impl.TopicFgPresenter;

import java.util.List;

import community.adapter.RecommendTopicAdapter;
import community.adapter.TopicAdapter;

public class TopicFragment extends RecommendTopicFragment {

    protected View mSearchLayout;
    private EditText mSearchEdit;
    private boolean mIsBackup = false;
    private InputMethodManager mInputMan;

    private boolean isFirstCreate = true;

    public TopicFragment() {
        super();
    }

    public static TopicFragment newTopicFragment() {
        return new TopicFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return ResFinder.getLayout("umeng_comm_topic_search");
    }

    @Override
    protected void initWidgets() {
        initRefreshView(mRootView);
        initSearchView(mRootView);
        initTitleView(mRootView);
        mInputMan = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected RecommendTopicPresenter createPresenters() {
        return new TopicFgPresenter(this);
    }

    @Override
    protected void initTitleView(View rootView) {
        int searchButtonResId = ResFinder.getId("umeng_comm_topic_search");
        rootView.findViewById(searchButtonResId).setOnClickListener(
                new Listeners.LoginOnViewClickListener() {

                    @Override
                    protected void doAfterLogin(View v) {
                        mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
                        mAdapter.backupData();
                        ((TopicFgPresenter) mPresenter).executeSearch(mSearchEdit
                                .getText().toString().trim());
                    }

                });
        rootView.findViewById(ResFinder.getId("umeng_comm_back")).setVisibility(View.GONE);
        int paddingRight = DeviceUtils.dp2px(getActivity(), 10);
        int paddingLeft = mSearchEdit.getPaddingLeft();
        int paddingTop = mSearchEdit.getPaddingTop();
        int paddingBottom = mSearchEdit.getPaddingBottom();
        mSearchEdit.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    @Override
    protected void initAdapter() {
        mAdapter = new TopicAdapter(getActivity());
        ((TopicAdapter) mAdapter).setFollowListener(new RecommendTopicAdapter.FollowListener<Topic>() {

            @Override
            public void onFollowOrUnFollow(Topic topic, ToggleButton toggleButton,
                                           boolean isFollow) {
                mPresenter.checkLoginAndExecuteOp(topic, toggleButton, isFollow);
            }
        });
        mTopicListView.setAdapter(mAdapter);
    }

    /**
     * 初始化搜索话题View跟事件处理</br>
     *
     * @param rootView
     */
    protected void initSearchView(View rootView) {
        mSearchLayout = findViewById(ResFinder.getId("umeng_comm_topic_search_title_layout"));
        int searchEditResId = ResFinder.getId("umeng_comm_topic_edittext");
        mSearchEdit = findViewById(searchEditResId);
        mSearchEdit.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    ((TopicFgPresenter) mPresenter).executeSearch(mSearchEdit.getText()
                            .toString().trim());
                }
                return false;
            }
        });

        // 话题本地搜索
        mSearchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s)) {
                    if (!mIsBackup) {
                        mIsBackup = true;
                        mAdapter.backupData();
                    }
                    // 如果keyword不为空，做本地搜索
                    List<Topic> result = mPresenter.localSearchTopic(s.toString());
                    mAdapter.updateListViewData(result);
                } else {
                    // 显示本地所有的话题
                    mAdapter.restoreData();
                    mIsBackup = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (isFirstCreate) {
                isFirstCreate = false; // 隐藏且为第一次创建Fragment（初始化时isVisibleToUser为不可见）
            } else {
                mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0); // 隐藏且是非第一次创建，则隐藏软键盘
            }
        }
    }

    @Override
    protected void initRefreshView(View rootView) {
        super.initRefreshView(rootView);
        mRefreshLvLayout.setProgressViewOffset(false, 60,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        mRefreshLvLayout.setRefreshing(true);
        mBaseView.setEmptyViewText(ResFinder.getString("umeng_comm_no_topic"));
    }

    @Override
    public void onPause() {
        mInputMan.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}
