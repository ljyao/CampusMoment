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

import com.bluelinelabs.logansquare.LoganSquare;
import com.uy.bbs.R;
import com.uy.util.Worker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.weixvn.http.AsyncWaeHttpClient;
import org.weixvn.util.NetworkHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import community.providable.LoginPrvdr;
import model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import provider.LoginDean;
import provider.NetWorkPrvdr;
import provider.ToastUtil;

@EActivity(R.layout.plugin_login)
public class DeanLogin extends AppCompatActivity implements OnClickListener,
        OnEditorActionListener {
    public static final String ACTIVITY_MARK = "activity_mark";
    public static AsyncWaeHttpClient deanHttpClient = new AsyncWaeHttpClient(true, 80, 443);
    @Extra
    public boolean isRegister = false;
    @ViewById(R.id.userid)
    EditText userIdEditText;
    @ViewById(R.id.password)
    EditText passwordEditText;
    @ViewById(R.id.sign_in_button)
    Button loginButton;
    private ProgressDialog progressDialog;
    private boolean isSetPassword = false;
    private String mUserId;
    private String mPassword;

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
        mUserId = userIdEditText.getText().toString();
        mPassword = passwordEditText.getText().toString();
        // Check for a valid mPassword, if the uer entered one.
        if (TextUtils.isEmpty(mPassword)) {
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
        if (isSetPassword) {
            if (isRegister) {
                register();
            } else {
                reSetPassword();
            }
        } else {
            progressDialog.setMessage("登录教务处...");
            progressDialog.show();
            loginDean();
        }
    }

    private void register() {
        NetWorkPrvdr netWorkPrvdr = new NetWorkPrvdr(this);
        Map<String, String> params = new HashMap<>();
        params.put("userid", mUserId);
        params.put("password", mPassword);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("注册中...");
        netWorkPrvdr.post(NetWorkPrvdr.register, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                final User.Pojo pojo = LoganSquare.parse(result, User.Pojo.class);
                Worker.postMain(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ToastUtil.showLongToast(DeanLogin.this, "注册成功！");
                  //      if (pojo.code == 0) {
                            LoginPrvdr loginPrvdr = new LoginPrvdr();
                            User user=new User();
                            user.userid=mUserId;
                            loginPrvdr.loginToUM(DeanLogin.this, user);
                            SetUserInfoActivity_.intent(DeanLogin.this).start();
                            DeanLogin.this.finish();
                  //      }
                    }
                });
            }
        });

    }

    private void reSetPassword() {
        NetWorkPrvdr netWorkPrvdr = new NetWorkPrvdr(this);
        Map<String, String> params = new HashMap<>();
        params.put("userid", mUserId);
        params.put("mPassword", mPassword);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("重置密码中...");
        netWorkPrvdr.post(NetWorkPrvdr.updateUser, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                String result = response.body().string();
                User.Pojo pojo = LoganSquare.parse(result, User.Pojo.class);
                ToastUtil.showLongToast(DeanLogin.this, pojo.reason);
                if (pojo.code == 0) {
                    SetUserInfoActivity_.intent(DeanLogin.this).start();
                    DeanLogin.this.finish();
                }

            }
        });
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
                ToastUtil.showLongToast(DeanLogin.this, "验证成功!");
                setPassword();
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

    private void setPassword() {
        isSetPassword = true;
        userIdEditText.setEnabled(false);
        passwordEditText.setText("");
        String titleStr;
        if (isRegister) {
            titleStr = "注册";
        } else {
            titleStr = "重设密码";
        }
        setTitle(titleStr);
        loginButton.setText(titleStr);
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

}
