package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.comm.core.beans.CommUser;
import com.uy.bbs.R;

import community.providable.LoginPrvdr;

public class LoginActivity extends AppCompatActivity {

    public View mLoginFormView;
    public SimpleDraweeView userHeader;
    // UI references.
    private EditText mUserId;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userHeader = (SimpleDraweeView) findViewById(R.id.use_header);


       /* // 获取CommunitySDK实例, 参数1为Context类型
        CommunitySDK mCommSDK = App.getCommunitySDK();
        // 打开微社区的接口, 参数1为Context类型
        mCommSDK.openCommunity(this);
        LoginSDKManager.getInstance().addAndUse(new LoginPrvdr());*/

        // Set up the login form.
        mUserId = (EditText) findViewById(R.id.userid);
        mUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
        String userId = mUserId.getText().toString();
        String password = mPasswordView.getText().toString();

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
            LoginPrvdr loginPrvdr = new LoginPrvdr();
            loginPrvdr.login(this, userId, new LoginPrvdr.UMLoginListener() {
                @Override
                public void onLoginSuccess(CommUser commUser) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity_.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onLoginFail(int stCode) {

                }
            });
        }
    }

}

