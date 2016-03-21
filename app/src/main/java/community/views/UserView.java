package community.views;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
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

@EViewGroup(R.layout.friends_item)
public class UserView extends RelativeLayout implements ViewWrapper.Binder<CommUser> {
    @ViewById(R.id.userhead_icon)
    public SimpleDraweeView mImageView;
    @ViewById(R.id.user_name)
    public TextView mTextView;
    private CommUser user;

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
    }

    @Click({R.id.userhead_icon, R.id.user_name})
    public void onClickUser() {
        UserDetailActivity_.intent(getContext()).user(user).start();
    }
}
