package activity;

import android.content.Intent;
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

import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import community.providable.LoginPrvdr;
import helper.common_util.SharePrefUtils;

public class LoginActivity extends AppCompatActivity {

    public View mLoginFormView;
    private EditText mUserId;
    private EditText mPasswordView;
    private SharePrefUtils sharePrefUtils;
    private TextView registerTv;

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
                DeanLogin_.intent(LoginActivity.this).start();
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
        LoginPrvdr loginPrvdr = new LoginPrvdr();
        loginPrvdr.login(this, userId, password, new LoginPrvdr.UMLoginListener() {
            @Override
            public void onLoginSuccess(CommUser commUser) {
                sharePrefUtils.putBoolean("loginStatus", true);
                sharePrefUtils.putString("userId", userId);
                sharePrefUtils.putString("password", password);
                Intent intent = new Intent(LoginActivity.this, MainActivity_.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLoginFail(int stCode) {
                mPasswordView.setError("密码错误");
                mPasswordView.requestFocus();
                mPasswordView.setText("");
            }
        });
    }

}

