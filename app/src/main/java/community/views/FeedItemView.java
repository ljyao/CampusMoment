package community.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.ImageItem;
import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.TimeUtils;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.comm.ui.activities.LocationFeedActivity;
import com.umeng.comm.ui.adapters.FeedImageAdapter;
import com.umeng.comm.ui.mvpview.MvpLikeView;
import com.umeng.comm.ui.presenter.impl.FeedContentPresenter;
import com.umeng.comm.ui.presenter.impl.LikePresenter;
import com.umeng.comm.ui.widgets.WrapperGridView;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Date;
import java.util.List;

import activity.UserDetailActivity_;
import adapter.ViewWrapper;
import community.fragment.FeedFragment;
import community.util.FeedViewRender;

/**
 * Created by ljy on 15/12/21.
 */
@EViewGroup(R.layout.feed_item_view)
public class FeedItemView extends RelativeLayout implements ViewWrapper.Binder<FeedItem>, MvpLikeView {
    private static final String M = "m";

    @ViewById(R.id.feed_type_img_btn)
    public ImageView mFeedTypeImgView;
    @ViewById(R.id.user_portrait_img_btn)
    public SimpleDraweeView mProfileImgView;
    @ViewById(R.id.umeng_comm_dialog_btn)
    public ImageView mShareBtn;
    @ViewById(R.id.umeng_comm_msg_user_name)
    public TextView mUserNameTv;
    @ViewById(R.id.umeng_comm_msg_text)
    public TextView mFeedTextTv;
    @ViewById(R.id.umeng_comm_msg_location)
    public ImageView mLocationImgView;
    @ViewById(R.id.umeng_comm_msg_location_text)
    public TextView mLocationTv;
    @ViewById(R.id.forward_image_gv_layout)
    public RelativeLayout mForwardLayout;
    @ViewById(R.id.umeng_comm_forard_text_tv)
    public TextView mForwardTextTv;
    @ViewById(R.id.umeng_comm_msg_images_gv_viewstub)
    public ViewStub mImageGvViewStub;
    public WrapperGridView mImageGv;
    @ViewById(R.id.feed_action_layout)
    public LinearLayout mButtomLayout;
    @ViewById(R.id.umeng_comm_msg_time_tv)
    public TextView mTimeTv;
    @ViewById(R.id.umeng_comm_like_tv)
    public TextView mLikeCountTextView;
    @ViewById(R.id.umeng_comm_forward_tv)
    public TextView mForwardCountTextView;
    @ViewById(R.id.umeng_comm_comment_tv)
    public TextView mCommentCountTextView;
    @ViewById(R.id.umeng_comm_distance)
    public TextView mDistanceTextView;
    protected FeedItem mFeedItem;

    FeedContentPresenter mPresenter;
    LikePresenter mLikePresenter;
    Listeners.OnItemViewClickListener<FeedItem> mItemViewClickListener;
    private FeedFragment.FeedListListener feedListListener;

    private String mContainerClzName;
    private boolean mIsShowDistance = false;
    /**
     * 点击地理位置的回调，跳转至LocationFeedActivity页面
     */
    private OnClickListener mLocationClickListener = new Listeners.LoginOnViewClickListener() {

        @Override
        protected void doAfterLogin(View v) {
            Intent intent = new Intent(getContext(), LocationFeedActivity.class);
            intent.putExtra(Constants.FEED, mFeedItem);
            getContext().startActivity(intent);
        }
    };

    public FeedItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void bind(FeedItem data) {
        mFeedItem = data;
        mFeedTextTv.setText(data.text);
        setFeedItem(data);
    }

    @AfterViews
    protected void initView() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                feedListListener.onShowFeedDetail(mFeedItem);
            }
        });
        initEventHandle();
        initPresenters();
    }

    protected void initPresenters() {
        mPresenter = new FeedContentPresenter();
        mPresenter.attach(getContext());
        mLikePresenter = new LikePresenter(this);
        mLikePresenter.attach(getContext());
        mLikePresenter.setFeedItem(mFeedItem);
    }


    private void setupImageGridView() {
        if (mFeedItem.getImages() != null && mFeedItem.getImages().size() > 0) {
            showImageGridView();
        } else {
            hideImageGridView();
        }
    }

    public void showImageGridView() {
        // 显示转发的布局
        mForwardLayout.setVisibility(View.VISIBLE);
        if (mImageGvViewStub.getVisibility() == View.GONE) {
            mImageGvViewStub.setVisibility(View.VISIBLE);
            mImageGv = (WrapperGridView) findViewById(R.id.umeng_comm_msg_gridview);
            mImageGv.hasScrollBar = true;
        }

        mImageGv.setBackgroundColor(Color.TRANSPARENT);
        mImageGv.setVisibility(View.VISIBLE);
        // adapter
        FeedImageAdapter gridviewAdapter = new FeedImageAdapter(getContext());
        gridviewAdapter.addDatasOnly(mFeedItem.getImages());
        // 设置图片
        mImageGv.setAdapter(gridviewAdapter);
        // 计算列数
        mImageGv.updateColumns(3);

        // 图片GridView
        mImageGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mPresenter.jumpToImageBrowser(mFeedItem.getImages(), pos);
            }
        });
    }

    private void hideImageGridView() {
        if (mImageGv != null) {
            mImageGv.setAdapter(new FeedImageAdapter(getContext()));
            mImageGv.setVisibility(View.GONE);
        }
    }


    protected void initEventHandle() {
        mLikeCountTextView.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void doAfterLogin(View v) {
                clickAnima(mLikeCountTextView);
                mLikePresenter.setFeedItem(mFeedItem);
                if (mFeedItem.isLiked) {
                    mLikePresenter.postUnlike(mFeedItem.id);
                } else {
                    mLikePresenter.postLike(mFeedItem.id);
                }
            }
        });

        // 转发按钮
        mForwardCountTextView.setOnClickListener(new Listeners.LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                clickAnima(mForwardCountTextView);
                mPresenter.gotoForwardActivity(mFeedItem);
            }
        });

        mLocationImgView.setOnClickListener(mLocationClickListener);
        mLocationTv.setOnClickListener(mLocationClickListener);

    }

    private void clickAnima(View targetView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f);
        scaleAnimation.setDuration(100);
        targetView.startAnimation(scaleAnimation);
    }

    public void setShareActivity(final Activity activity) {
        mShareBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                shareToSns(activity);
            }
        });
    }

    private void shareToSns(Activity activity) {
        ShareContent shareItem = new ShareContent();
        shareItem.mText = mFeedItem.text;
        List<ImageItem> imageItems = mFeedItem.imageUrls;
        if (mFeedItem.sourceFeed != null) {
            imageItems = mFeedItem.sourceFeed.imageUrls;
        }
        if (imageItems.size() > 0) {
            shareItem.mImageItem = imageItems.get(0);
        }
        shareItem.mTargetUrl = mFeedItem.shareLink;
        if (TextUtils.isEmpty(shareItem.mTargetUrl) && mFeedItem.sourceFeed != null) {
            shareItem.mTargetUrl = mFeedItem.sourceFeed.shareLink;
        }
        shareItem.mFeedId = mFeedItem.id;
        shareItem.mTitle = mFeedItem.text;
        ShareSDKManager.getInstance().getCurrentSDK().share(activity, shareItem);
    }

    /**
     * 填充消息流ListView每项的数据
     */
    protected void bindFeedItemData() {

        if (TextUtils.isEmpty(mFeedItem.id)) {
            return;
        }
        // 设置基础信息
        setBaseFeeditemInfo();
        // 设置图片
        setupImageGridView();
        // 设置feed图片
        // 转发的feed
        if (mFeedItem.sourceFeed != null) {
            // 转发视图
            setForwardViewVisibility(mFeedItem);
            // 设置转发视图的数据
            setForwardItemData(mFeedItem);
        } else {
            // 设置普通类型feed的item view的可见性
            setCommFeedViewVisibility(mFeedItem);
        }

        if (mFeedItem.likeCount == 0) {
            mLikeCountTextView.setText("赞");
        } else {
            mLikeCountTextView.setText("" + mFeedItem.likeCount);
        }
        if (mFeedItem.commentCount != 0) {
            mCommentCountTextView.setText("" + mFeedItem.commentCount);
        } else {
            mCommentCountTextView.setText("评论");

        }
        if (mFeedItem.forwardCount != 0) {
            mForwardCountTextView.setText("" + mFeedItem.forwardCount);
        } else {
            mForwardCountTextView.setText("转发");
        }
        like(mFeedItem.isLiked);
    }

    /**
     * 设置普通feed视图的可见性
     */
    private void setCommFeedViewVisibility(FeedItem item) {
        // 修改转发视图的背景为透明
        mForwardLayout.setBackgroundColor(Color.TRANSPARENT);
        // mForwardLayout.setVisibility(View.GONE);
        mForwardLayout.setPadding(0, 0, 0, 0);
        if (mImageGv != null) {
            mImageGv.setPadding(0, 0, 0, 0);
        }
        // 隐藏转发视图
        mForwardTextTv.setVisibility(View.GONE);

        // 显示时间视图
        mTimeTv.setVisibility(View.VISIBLE);
        // 昵称
        mUserNameTv.setVisibility(View.VISIBLE);
        // 加载头像视图设置为可见
        mProfileImgView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置转发的数据
     */
    private void setForwardItemData(FeedItem item) {
        // @原始feed的创建者
        atOriginFeedCreator(item.sourceFeed);
        // 大于等于2表示该feed已经被删除
        if (item.sourceFeed.status >= FeedItem.STATUS_SPAM || isDeleted(item.sourceFeed)) {
            mForwardTextTv.setGravity(Gravity.CENTER);
            mForwardTextTv.setText(ResFinder.getString("umeng_comm_feed_deleted"));
            if (mImageGv != null) {
                mImageGv.setVisibility(View.GONE);
            }
            // 如果该feed是收藏，且转发feed、原feed都被删除，则不显示原feed的状态
            if (item.status >= FeedItem.STATUS_SPAM
                    && item.sourceFeed.status >= FeedItem.STATUS_SPAM) {
                mForwardLayout.setVisibility(View.GONE);
            } else {
                // 删除被转发的feed
                deleteInvalidateFeed(item.sourceFeed);
            }
        } else {
            mForwardTextTv.setGravity(Gravity.LEFT | Gravity.CENTER);
            // 解析被转发的@和话题
            FeedViewRender.parseTopicsAndFriends(mForwardTextTv, item.sourceFeed);
            if (mImageGv != null) {
                mImageGv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * @param feedItem
     */
    protected void deleteInvalidateFeed(FeedItem feedItem) {
        DatabaseAPI.getInstance().getFeedDBAPI().deleteFeedFromDB(feedItem.id);
    }

    /**
     * 被转发的原始feed的创建者在转发时会被@,因此将其名字设置到文本中,然后将其添加到@的好友中.
     *
     * @param feedItem
     */
    protected void atOriginFeedCreator(FeedItem feedItem) {
        String contextText = feedItem.text;
        // @前缀
        final String atPrefix = "@" + feedItem.creator.name + ": ";
        if (!contextText.contains(atPrefix)) {
            feedItem.text = atPrefix + contextText;
            feedItem.atFriends.add(feedItem.creator);
        }
    }

    /**
     * 判断该feed是否被删除，本地[目前暂时按照从方法判断]</br>
     *
     * @param item
     * @return
     */
    private boolean isDeleted(FeedItem item) {
        return TextUtils.isEmpty(item.publishTime);
    }

    /**
     * 设置转发feed的视图的可见性
     */
    @SuppressWarnings("deprecation")
    private void setForwardViewVisibility(FeedItem item) {
        // 显示转发视图
        mForwardLayout.setVisibility(View.VISIBLE);
        mForwardLayout.setPadding(10, 10, 10, 10);
        mForwardLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPresenter.clickOriginFeedItem(mFeedItem);
            }
        });
        if (mImageGv != null) {
            mImageGv.setPadding(10, 2, 10, 10);
        }

        // 转发视图的背景
        mForwardLayout.setBackgroundColor(getResources().getColor(R.color.white_grey));
        // 被转发的文本
        mForwardTextTv.setVisibility(View.VISIBLE);
        mForwardTextTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPresenter.clickOriginFeedItem(mFeedItem);
            }
        });

        // 隐藏位置图标
        mLocationImgView.setVisibility(View.GONE);
        mLocationTv.setVisibility(View.GONE);
    }

    /**
     * 设置feedItem的基本信息（头像，昵称，内容、位置）</br>
     */
    protected void setBaseFeeditemInfo() {
        // 设置feed类型图标
        setTypeIcon();
        // 用户头像
        mProfileImgView.setImageURI(Uri.parse(mFeedItem.creator.iconUrl));

        // 昵称
        mUserNameTv.setText(mFeedItem.creator.name);
        // 更新时间
        Date date = new Date(Long.parseLong(mFeedItem.publishTime));
        mTimeTv.setText(TimeUtils.format(date));
        // feed的文本内容
        FeedViewRender.parseTopicsAndFriends(mFeedTextTv, mFeedItem);

        // 地理位置信息
        if (TextUtils.isEmpty(mFeedItem.locationAddr)) {
            mLocationTv.setVisibility(View.GONE);
            mLocationImgView.setVisibility(View.GONE);
        } else {
            mLocationTv.setVisibility(View.VISIBLE);
            mLocationImgView.setVisibility(View.VISIBLE);
            mLocationTv.setText(mFeedItem.locationAddr);
        }

        // 内容为空时Text隐藏布局,这种情况出现在转发时没有文本的情况
        if (TextUtils.isEmpty(mFeedItem.text)) {
            mFeedTextTv.setVisibility(View.GONE);
        } else {
            mFeedTextTv.setVisibility(View.VISIBLE);
        }
        mFeedTextTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mFeedItem.status >= FeedItem.STATUS_SPAM && mFeedItem.status != FeedItem.STATUS_LOCK) {
                    ToastMsg.showShortMsgByResName("umeng_comm_feed_spam_deleted");
                    return;
                } else {
                    feedListListener.onShowFeedDetail(mFeedItem);
                }
            }
        });
        if (mIsShowDistance) {
            mDistanceTextView.setText(mFeedItem.distance + M);
        }
    }

    /**
     * 设置feed 类型的icon
     */
    private void setTypeIcon() {
        Drawable drawable = null;
        if (mFeedItem.type == FeedItem.ANNOUNCEMENT_FEED
                && mFeedItem.isTop == FeedItem.TOP_FEED) {
            // 设置feed类型图标
            drawable = ResFinder.getDrawable("umeng_comm_top_announce");
        } else if (mFeedItem.type == FeedItem.ANNOUNCEMENT_FEED) {
            // 设置feed类型图标
            drawable = ResFinder.getDrawable("umeng_comm_announce");
        } else if (mFeedItem.isTop == FeedItem.TOP_FEED) {
            // feed 置顶图标
            drawable = ResFinder.getDrawable("umeng_comm_top");
            mFeedTypeImgView.setVisibility(View.VISIBLE);
            mFeedTypeImgView.setImageDrawable(drawable);
        } else {
            // 设置feed类型图标 [ 目前只标识公告类型 ]
            mFeedTypeImgView.setVisibility(View.INVISIBLE);
            return;
        }

        mFeedTypeImgView.setVisibility(View.VISIBLE);
        mFeedTypeImgView.setImageDrawable(drawable);

    }


    public FeedItem getFeedItem() {
        return mFeedItem;
    }

    public void setFeedItem(FeedItem feedItem) {
        mFeedItem = feedItem;
        mPresenter.setFeedItem(mFeedItem);
        bindFeedItemData();
    }

    /**
     * 在feed详情页面隐藏赞、评论、转发三个按钮
     */
    public void hideActionButtons() {
        mLikeCountTextView.setVisibility(View.GONE);
        mCommentCountTextView.setVisibility(View.GONE);
        mForwardCountTextView.setVisibility(View.GONE);
    }

    public void setOnItemViewClickListener(final int position, final Listeners.OnItemViewClickListener<FeedItem> listener) {
        mItemViewClickListener = listener;
        mCommentCountTextView.setOnClickListener(new Listeners.LoginOnViewClickListener() {

            @Override
            protected void doAfterLogin(View v) {
                clickAnima(mCommentCountTextView);
                if (mItemViewClickListener != null) {
                    mItemViewClickListener.onItemClick(position, mFeedItem);
                }
            }
        });
    }

    public void setOnUpdateListener(Listeners.OnResultListener listener) {
        mLikePresenter.setResultListener(listener);
    }

    @Override
    public void like(boolean isLiked) {
        mFeedItem.isLiked = isLiked;
        if (mFeedItem.isLiked) {
            mLikeCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.toolbar_icon_like, 0, 0, 0);
        } else {
            mLikeCountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.toolbar_icon_unlike, 0, 0, 0);
        }
    }

    @Override
    public void updateLikeView(String nextUrl) {
        if (mFeedItem.likeCount == 0) {
            mLikeCountTextView.setText("赞");
        } else {
            mLikeCountTextView.setText("" + mFeedItem.likeCount);
        }
    }

    public void setShowDistance() {
        mIsShowDistance = true;
        mDistanceTextView.setVisibility(View.VISIBLE);
    }

    public void setListener(FeedFragment.FeedListListener feedListListener) {
        this.feedListListener = feedListListener;
    }

    @Click(R.id.user_portrait_img_btn)
    public void onClickUserHead() {
        UserDetailActivity_.intent(getContext()).user(mFeedItem.creator).start();
    }
}
