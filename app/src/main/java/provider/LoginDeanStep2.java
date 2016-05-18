package provider;

import org.apache.http.Header;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.weixvn.http.AsyncWaeHttpClient;
import org.weixvn.http.AsyncWaeHttpRequest;
import org.weixvn.http.AsyncWaeHttpRequest.RequestType;
import org.weixvn.http.JsoupHttpRequestResponse;

@SuppressWarnings("deprecation")
public class LoginDeanStep2 extends JsoupHttpRequestResponse {
    private static final String URI = "https://matrix.dean.swust.edu.cn/cas/login";

    @Override
    final public void onRequest(AsyncWaeHttpRequest request) {
        String lt = (String) getHttpClient().getCache(LoginDeanStep1.LT);
        String service = (String) getHttpClient().getCache(
                LoginDeanStep1.SERVICE);
        String username = (String) getHttpClient().getCache(LoginDean.USERNAME);
        String password = (String) getHttpClient().getCache(LoginDean.PASSWORD);

        request.setRequestURI(URI);
        request.setRequestType(RequestType.POST);
        request.getRequestParams().put("lt", lt);
        request.getRequestParams().put("service", service);
        request.getRequestParams().put("username", username);
        request.getRequestParams().put("password", password);
        getHttpClient().addHeader(AsyncWaeHttpClient.HEADER_ACCEPT_LANGUAGE,
                AsyncWaeHttpClient.LANGUAGE_CN);
        getHttpClient().addHeader(AsyncWaeHttpClient.HEADER_ACCEPT_ENCODING,
                AsyncWaeHttpClient.ENCODING_GZIP);
    }

    @Override
    public void doResponse(int statusCode, Header[] headers, Document doc) {
        Element notify = doc.getElementsByClass("notify").first();
        Element alert = doc.getElementsByClass("alert").first();

        if (notify != null && notify.text().contains("登录成功")) {
            // 登录成功，等待跳转
            Element element = doc.getElementsByTag("a").first();
            String uri = element.attr("href");
            getHttpClient().putCache(LoginDean.HREF, uri);
            getHttpClient().putCache(LoginDean.STATE, LoginDean.SUCCESS);
        } else if (alert != null && alert.text().contains("用户名和密码不匹配")) {
            getHttpClient().putCache(LoginDean.STATE, LoginDean.PASSWORD_ERROR);
        } else {
            getHttpClient().putCache(LoginDean.STATE, LoginDean.SERVER_ERROR);
        }
    }

}
