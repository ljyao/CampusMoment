package activity;

import android.app.Activity;
import android.content.Intent;

import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import community.providable.LoginPrvdr;
import helper.common_util.SharePrefUtils;
import model.User;

/**
 * Created by Shine on 2016/2/12.
 */
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends Activity {
    private SharePrefUtils sharePrefUtils;

    @AfterViews
    public void init() {
        loginOrFeed();
    }


    public void loginOrFeed() {

        User user = User.getCurrentUser(this);
        final Intent intent;
        if (user != null) {
            intent = new Intent(this, MainActivity_.class);
            LoginPrvdr loginPrvdr = new LoginPrvdr();
            loginPrvdr.loginToUM(this, user);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}
