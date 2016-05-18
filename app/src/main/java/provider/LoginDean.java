
package provider;

import android.util.Log;

import org.apache.http.Header;
import org.jsoup.nodes.Document;
import org.weixvn.http.AsyncWaeHttpClient;
import org.weixvn.http.SeriesHttpRequestResponse;

import activity.DeanLogin;


public abstract class LoginDean extends SeriesHttpRequestResponse {
    public static final String STATE = "login_state";
    public static final String USERNAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String HREF = "href";
    public static final String SUCCESS = "请求成功";
    public static final String PASSWORD_ERROR = "用户名或密码错误";
    public static final String NETWORK_ERROR = "教务系统维护中,请稍候再试";
    public static final String SERVER_ERROR = "服务器返回异常";
    public static final String USER_HAS_LOGIN = "用户已经登录";
    private static final String TAG = LoginDean.class.getSimpleName();
    private boolean isRegist;

    public LoginDean(AsyncWaeHttpClient httpClient, boolean isRegist) {
        super(httpClient);
        this.isRegist = isRegist;
    }

    @Override
    public final void run() {
        if (httpClient == null) {
            Log.e(TAG, "http client can't be null!");
        }

        httpClient.execute(new LoginDeanStep1() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, Document doc) {
                super.onSuccess(statusCode, headers, doc);

                String state = (String) httpClient.getCache(STATE);
                // 用户已经登录
                if (USER_HAS_LOGIN.equals(state)) {
                    LoginDean.this.onSuccess();
                    return;
                }
                // 服务器异常
                if (SERVER_ERROR.equals(state)) {
                    failure(state);
                    return;
                }

                httpClient.execute(new LoginDeanStep2() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          Document doc) {
                        super.onSuccess(statusCode, headers, doc);
                        String state = (String) httpClient.getCache(STATE);
                        if (!SUCCESS.equals(state)) {
                            failure(state);
                            return;
                        }

                        httpClient.execute(new LoginDeanStep3(isRegist) {
                            @Override
                            public void onSuccess(int statusCode,
                                                  Header[] headers, Document doc) {
                                super.onSuccess(statusCode, headers, doc);

                                String state = (String) httpClient
                                        .getCache(STATE);
                                if (!SUCCESS.equals(state)) {
                                    failure(state);
                                    return;
                                }
                                LoginDean.this.onSuccess();
                            }

                            @Override
                            public void onFailure(int statusCode,
                                                  Header[] headers, Document doc,
                                                  Throwable throwable) {
                                super.onFailure(statusCode, headers, doc,
                                        throwable);

                                failure(NETWORK_ERROR);
                            }
                        });

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Document doc, Throwable throwable) {
                        super.onFailure(statusCode, headers, doc, throwable);

                        failure(NETWORK_ERROR);
                    }

                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Document doc, Throwable throwable) {
                super.onFailure(statusCode, headers, doc, throwable);

                failure(NETWORK_ERROR);
            }

        });

    }

    private void failure(String error) {
        LoginDean.this.onFailure(error);
        DeanLogin.deanHttpClient = new AsyncWaeHttpClient(true, 80, 443);
        DeanLogin.deanHttpClient.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586");
    }

}
