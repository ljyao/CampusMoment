package activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.uy.bbs.R;
import com.uy.util.Worker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import community.providable.LoginPrvdr;
import helper.common_util.SharePrefUtils;
import model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import provider.NetWorkPrvdr;
import provider.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    public View mLoginFormView;
    private EditText mUserId;
    private EditText mPasswordView;
    private SharePrefUtils sharePrefUtils;
    private TextView registerTv;
    private TextView reSetPasswordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登录");
        sharePrefUtils = new SharePrefUtils("user", LoginActivity.this);
        registerTv = (TextView) findViewById(R.id.register);
        registerTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeanLogin_.intent(LoginActivity.this).isRegister(true).start();
            }
        });
        reSetPasswordTv = (TextView) findViewById(R.id.reset_password);
        reSetPasswordTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeanLogin_.intent(LoginActivity.this).isRegister(false).start();
            }
        });
        mUserId = (EditText) findViewById(R.id.userid);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    private void attemptLogin() {
        // Reset errors.
        mUserId.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String userId = mUserId.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the uer entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userId address.
        if (TextUtils.isEmpty(userId)) {
            mUserId.setError(getString(R.string.error_field_required));
            focusView = mUserId;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            login(userId, password);
        }
    }

    private void login(final String userId, final String password) {
        NetWorkPrvdr netWorkPrvdr = new NetWorkPrvdr(this);
        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("password", password);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("登录中...");
        netWorkPrvdr.post(NetWorkPrvdr.login, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                String result = response.body().string();
                final User.Pojo pojo = LoganSquare.parse(result, User.Pojo.class);
                ToastUtil.showLongToast(LoginActivity.this, pojo.reason);
                if (pojo.code == 0) {
                    User.updateUser(LoginActivity.this, pojo.user);
                    Worker.postMain(new Runnable() {
                        @Override
                        public void run() {
                            LoginPrvdr loginPrvdr = new LoginPrvdr();
                            loginPrvdr.login(LoginActivity.this, pojo.user, null);
                            MainActivity_.intent(LoginActivity.this).start();
                            finish();
                        }
                    });

                }
            }
        });

    }

}

