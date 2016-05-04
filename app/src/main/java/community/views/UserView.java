package community.views;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import activity.UserDetailActivity_;
import adapter.ViewWrapper;
import community.fragment.FollowedUserFragment;
import helper.common_util.AlertDialogUtils;

@EViewGroup(R.layout.friends_item)
public class UserView extends RelativeLayout implements ViewWrapper.Binder<CommUser> {
    private static final String DIVIDER = " / ";
    @ViewById(R.id.userhead_icon)
    public SimpleDraweeView mImageView;
    @ViewById(R.id.user_name)
    public TextView mTextView;
    @ViewById(R.id.follow_iv)
    public ImageView followIv;
    @ViewById(R.id.user_msg)
    public TextView userMsg;
    private CommUser user;
    private FollowedUserFragment.OnClickFollowListener followListener;

    public UserView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void bind(CommUser user) {
        this.user = user;
        if (user.gender == CommUser.Gender.MALE) {
            mImageView.setBackgroundResource(R.drawable.umeng_comm_male);
        } else {
            mImageView.setBackgroundResource(R.drawable.umeng_comm_female);
        }
        mImageView.setImageURI(Uri.parse(user.iconUrl));
        mTextView.setText(user.name);
        if (user.isFollowed && user.isFollowingMe) {
            followIv.setBackgroundResource(R.drawable.card_icon_arrow);
        } else if (user.isFollowed) {
            followIv.setBackgroundResource(R.drawable.card_icon_attention);
        } else {
            followIv.setBackgroundResource(R.drawable.card_icon_addattention);
        }
        userMsg.setText(buildMsgFansStr());
    }

    protected String buildMsgFansStr() {
        StringBuilder builder = new StringBuilder("微博：");
        builder.append(user.feedCount);
        builder.append(DIVIDER).append("粉丝");
        builder.append(user.fansCount);
        return builder.toString();
    }

    @Click(R.id.follow_iv)
    public void onClickFollow() {
        if (user.isFollowed) {
            AlertDialogUtils.builder(getContext()).setContent("取消关注该用户?")
                    .setOnPositiveClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            followListener.OnClickUnFollow(user);
                            AlertDialogUtils.dismiss();
                        }
                    }).show();
        } else {
            followListener.OnClickFollow(user);
        }
    }

    @Click({R.id.userhead_icon, R.id.user_name})
    public void onClickUser() {
        UserDetailActivity_.intent(getContext()).user(user).start();
    }

    public void setFollowListener(FollowedUserFragment.OnClickFollowListener followListener) {
        this.followListener = followListener;
    }
}
