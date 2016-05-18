package activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.uy.bbs.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.weixvn.http.AsyncWaeHttpClient;
import org.weixvn.util.NetworkHelper;

import provider.LoginDean;

@EActivity(R.layout.plugin_login)
public class DeanLogin extends AppCompatActivity implements OnClickListener,
        OnEditorActionListener {
    public static final String ACTIVITY_MARK = "activity_mark";
    public static AsyncWaeHttpClient deanHttpClient = new AsyncWaeHttpClient(true, 80, 443);
    @ViewById(R.id.userid)
    EditText userIdEditText;
    @ViewById(R.id.password)
    EditText passwordEditText;
    @ViewById(R.id.sign_in_button)
    Button loginButton;
    private ProgressDialog progressDialog;

    @AfterViews
    protected void initViews() {
        setTitle("登录教务处");
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        passwordEditText.setOnEditorActionListener(this);
        loginButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(DeanLogin.this);
        deanHttpClient.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                DeanLogin.this.finish();
                break;
            default:
                break;
        }
        return false;
    }


    /**
     * 准备登陆教务处
     */
    private void prepareLogin() {
        boolean cancel = false;
        View focusView = null;
        String mUserId = userIdEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        // Check for a valid password, if the uer entered one.
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid userId address.
        if (TextUtils.isEmpty(mUserId)) {
            userIdEditText.setError(getString(R.string.error_field_required));
            focusView = userIdEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        if (NetworkHelper.isNetworkAvailable(DeanLogin.this) == false) {
            Toast.makeText(DeanLogin.this, R.string.network_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(
                R.string.login_logining));
        loginDean();
    }

    /**
     * 登录教务处
     */
    private void loginDean() {
        String mUserId = userIdEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Reset errors.
        userIdEditText.setError(null);
        passwordEditText.setError(null);

        deanHttpClient.putCache(LoginDean.USERNAME, mUserId);
        deanHttpClient.putCache(LoginDean.PASSWORD, password);
        new LoginDean(deanHttpClient, false) {

            @Override
            public void onSuccess() {

                progressDialog.cancel();

                DeanLogin.this.finish();
            }

            @Override
            public void onFailure(String error) {
                if (error.equals(LoginDean.PASSWORD_ERROR)) {
                    passwordEditText.setText("");
                }
                progressDialog.cancel();
                Toast.makeText(DeanLogin.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        }.run();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                prepareLogin();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            loginButton.performClick();
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
