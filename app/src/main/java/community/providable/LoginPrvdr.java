package community.providable;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.uy.App;

import model.User;

/**
 * Created by ljy on 15/12/21.
 */
public class LoginPrvdr extends AbsLoginImpl {
    private CommunitySDK sdk = App.getCommunitySDK();
    private UMLoginListener loginCallBack;
    private CommUser loginedUser;
    private Context context;


    public void login(Context context, User user, @Nullable UMLoginListener listener) {
        this.loginCallBack = listener;
        loginToUM(context, user);
        this.context = context;
    }

    public void loginToUM(Context context, User user) {
        loginedUser = new CommUser(user.userid); // 用户id
        if (TextUtils.isEmpty(user.nickname)) {
            loginedUser.name = user.userid;
        } else {
            loginedUser.name = user.nickname; // 用户昵称
        }
        loginedUser.source = Source.SELF_ACCOUNT;// 登录系统来源

        LoginSDKManager.getInstance().addAndUse(this);

        sdk.login(context, new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int i, CommUser user) {
                loginedUser = user;
                if (loginCallBack != null) {
                    loginCallBack.onLoginSuccess(loginedUser);
                }

            }
        });
    }

    @Override
    protected void onLogin(Context context, LoginListener loginListener) {
        // 登录完成回调给社区SDK，200代表登录成功
        loginListener.onComplete(200, loginedUser);
    }


    public static abstract class UMLoginListener {
        public abstract void onLoginSuccess(CommUser commUser);

        public abstract void onLoginFail(int stCode);
    }
}
