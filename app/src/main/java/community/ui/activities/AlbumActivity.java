/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package community.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.imageloader.cache.ImageCache;
import com.umeng.comm.core.utils.ResFinder;

import java.util.List;

import community.imagepicker.widgets.RefreshGvLayout;
import community.imagepicker.widgets.RefreshLayout;
import community.ui.adapters.FeedImageAdapter;
import community.ui.adapters.UserAlbumAdapter;
import community.ui.dialogs.ImageBrowser;
import community.ui.mvpview.MvpAlbumView;
import community.ui.presenter.impl.AlbumPresenter;
import community.ui.utils.ViewFinder;

/**
 * 用户相册Activity
 *
 * @author mrsimple
 */
public class AlbumActivity extends Activity implements MvpAlbumView {

    RefreshGvLayout mRefreshGvLayout;
    FeedImageAdapter mImageAdapter;
    GridView mAlbumGridView;

    AlbumPresenter mPresenter;
    ImageBrowser mImageBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(ResFinder.getLayout("umeng_comm_album_layout"));
        initTitleLayout();

        mImageBrowser = new ImageBrowser(this);
        // 构建AlbumPresenter，参数1为用户id
        mPresenter = new AlbumPresenter(getIntent().getStringExtra(Constants.USER_ID_KEY), this);
        mPresenter.attach(getApplicationContext());
    }

    private void initTitleLayout() {
        ViewFinder viewFinder = new ViewFinder(getWindow().getDecorView());

        Button settingButton = viewFinder.findViewById(ResFinder.getId("umeng_comm_save_bt"));
        // 目前将设置功能移到发现页面，此时暂时隐藏设置按钮
        settingButton.setVisibility(View.INVISIBLE);

        findViewById(ResFinder.getId("umeng_comm_setting_back")).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        TextView titleTextView = (TextView) findViewById(ResFinder
                .getId("umeng_comm_setting_title"));
        titleTextView.setText("相册");

        // 初始化下拉刷新Layout
        mRefreshGvLayout = viewFinder
                .findViewById(ResFinder.getId("umeng_comm_album_swipe_layout"));
        mRefreshGvLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPresenter.loadDataFromServer();
            }
        });

        mRefreshGvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                mPresenter.loadMore();
            }
        });

        // 初始化GridView
        mAlbumGridView = viewFinder.findViewById(ResFinder.getId("umeng_comm_user_albun_gv"));
        mAlbumGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jumpToImageBrowser(mImageAdapter.getDataSource(), position);
            }
        });
        mImageAdapter = new UserAlbumAdapter(getApplicationContext());
        mAlbumGridView.setAdapter(mImageAdapter);
    }

    private void jumpToImageBrowser(List<ImageItem> images, int position) {
        mImageBrowser.setImageList(images, position);
        mImageBrowser.show();
    }

    @Override
    public void fetchedAlbums(List<ImageItem> imageItems) {
        mImageAdapter.addData(imageItems);

        mRefreshGvLayout.setRefreshing(false);
        mRefreshGvLayout.setLoading(false);
    }

    @Override
    public List<ImageItem> getBindDataSource() {
        return mImageAdapter.getDataSource();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshGvLayout.setRefreshing(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageCache.getInstance().clearLruCache();
        mPresenter.onDestroy();
    }
}
