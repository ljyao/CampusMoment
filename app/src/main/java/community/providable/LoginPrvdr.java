package community.providable;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.uy.App;
import com.uy.bbs.R;

/**
 * Created by ljy on 15/12/21.
 */
public class LoginPrvdr extends AbsLoginImpl {
    public ProgressDialog dialog;
    private CommunitySDK sdk = App.getCommunitySDK();
    private String userId;
    private UMLoginListener loginCallBack;
    private CommUser loginedUser;
    private Context context;
    private String password;

    public void login(Context context, String userId, String password, @Nullable UMLoginListener listener) {
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.logining));
        this.userId = userId;
        this.loginCallBack = listener;
        this.password = password;
        loginToUM(context, userId);
        this.context = context;

    }

    public void loginToUM(Context context, String userId) {

        loginedUser = new CommUser(userId); // 用户id
        loginedUser.name = userId; // 用户昵称
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
