package provider;

import org.apache.http.Header;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.weixvn.http.AsyncWaeHttpClient;
import org.weixvn.http.AsyncWaeHttpRequest;
import org.weixvn.http.AsyncWaeHttpRequest.RequestType;
import org.weixvn.http.JsoupHttpRequestResponse;

@SuppressWarnings("deprecation")
public class LoginDeanStep1 extends JsoupHttpRequestResponse {
    public static final String LT = "lt";
    public static final String SERVICE = "service";
    private static final String URI = "https://matrix.dean.swust.edu.cn/acadmicManager/index.cfm?event=studentPortal:DEFAULT_EVENT";

    @Override
    final public void onRequest(AsyncWaeHttpRequest request) {
        request.setRequestURI(URI);
        request.setRequestType(RequestType.GET);
        getHttpClient().addHeader(AsyncWaeHttpClient.HEADER_ACCEPT_LANGUAGE,
                AsyncWaeHttpClient.LANGUAGE_CN);
        getHttpClient().addHeader(AsyncWaeHttpClient.HEADER_ACCEPT_ENCODING,
                AsyncWaeHttpClient.ENCODING_GZIP);
    }

    @Override
    public void doResponse(int statusCode, Header[] headers, Document doc) {
        try {
            // 判断是否已经登录
            Element navHome = doc.getElementById("navHome");
            if (navHome != null && "主页".equals(navHome.text())) {
                getHttpClient().putCache(LoginDean.STATE,
                        LoginDean.USER_HAS_LOGIN);
                return;
            }

            // 解析登录所需数据
            String lt = doc.select("input[name=lt]").first().attr("value");
            String service = doc.select("input[name=service]").first()
                    .attr("value");
            getHttpClient().putCache(LT, lt);
            getHttpClient().putCache(SERVICE, service);

        } catch (Exception e) {
            getHttpClient().putCache(LoginDean.STATE, LoginDean.SERVER_ERROR);
            e.printStackTrace();
        }
    }

}
