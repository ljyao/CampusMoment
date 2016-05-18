package provider;

import org.apache.http.Header;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.weixvn.http.AsyncWaeHttpClient;
import org.weixvn.http.AsyncWaeHttpRequest;
import org.weixvn.http.AsyncWaeHttpRequest.RequestType;
import org.weixvn.http.JsoupHttpRequestResponse;


/**
 * @author wcy
 */
@SuppressWarnings("deprecation")
public class LoginDeanStep3 extends JsoupHttpRequestResponse {
    private boolean isRegist;

    public LoginDeanStep3(boolean isRegist) {
        this.isRegist = isRegist;
    }

    @Override
    public void onRequest(AsyncWaeHttpRequest request) {
        request.setRequestURI((String) getHttpClient().getCache(LoginDean.HREF));
        request.setRequestType(RequestType.GET);
        getHttpClient().addHeader(AsyncWaeHttpClient.HEADER_ACCEPT_LANGUAGE,
                AsyncWaeHttpClient.LANGUAGE_CN);
        getHttpClient().addHeader(AsyncWaeHttpClient.HEADER_ACCEPT_ENCODING,
                AsyncWaeHttpClient.ENCODING_GZIP);
    }

    @Override
    public void doResponse(int statusCode, Header[] headers, Document doc) {
        analyze(doc);
    }

    private void analyze(Document doc) {
        Element navHome = doc.getElementById("navHome");
        if (navHome != null && "主页".equals(navHome.text())) {
            // 登录成功
            getHttpClient().putCache(LoginDean.STATE, LoginDean.SUCCESS);
            if (isRegist) {
                return;
            }
            String name = doc.getElementById("navAccountName").text();
            System.out.println("用户名：" + name);
        } else {
            getHttpClient().putCache(LoginDean.STATE, LoginDean.SERVER_ERROR);
        }
    }

}
