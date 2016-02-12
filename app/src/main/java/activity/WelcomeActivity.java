package activity;

import android.app.Activity;
import android.content.Intent;

import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import helper.util.SharePrefUtils;

/**
 * Created by Shine on 2016/2/12.
 */
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends Activity {
    private SharePrefUtils sharePrefUtils;

    @AfterViews
    public void init() {
        sharePrefUtils = new SharePrefUtils("user", this);
        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                loginOrFeed();
            }
        }, 3000);
    }


    public void loginOrFeed() {
        boolean status = sharePrefUtils.getBoolean("loginStatus");
        Intent intent;
        if (status) {
            intent = new Intent(this, MainActivity_.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
