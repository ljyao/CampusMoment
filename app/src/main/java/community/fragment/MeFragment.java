package community.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import activity.UserDetailActivity_;
import community.providable.NetLoaderListener;
import community.providable.UserPrvdr;

/**
 * Created by ljy on 15/12/25.
 */
@EFragment(R.layout.me_fragment)
public class MeFragment extends Fragment {
    @ViewById(R.id.userheader)
    protected SimpleDraweeView userHeader;
    @ViewById(R.id.username)
    protected TextView userName;
    @ViewById(R.id.feed_num_txt)
    protected TextView feedNum;
    @ViewById(R.id.follow_num_txt)
    protected TextView followNum;
    @ViewById(R.id.fans_txt)
    protected TextView fansNum;
    private CommUser user;
    private Context context;

    @AfterViews
    public void initView() {
        user = CommConfig.getConfig().loginedUser;
        Uri uri = Uri.parse(user.iconUrl);
        userHeader.setImageURI(uri);
        userName.setText(user.name);
        UserPrvdr userPrvdr = new UserPrvdr();
        userPrvdr.getUserInfo(user, new NetLoaderListener<CommUser>() {
            @Override
            public void onComplete(boolean statue, CommUser result) {
                feedNum.setText(user.feedCount + "");
                followNum.setText(user.followCount + "");
                fansNum.setText(user.fansCount + "");
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Click(R.id.use_header_rl)
    public void onClickUser() {
        Intent startIntent = new Intent(getActivity(), UserDetailActivity_.class);
        startActivity(startIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userHeader != null) {
            user = CommConfig.getConfig().loginedUser;
            userHeader.setImageURI(Uri.parse(user.iconUrl));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
