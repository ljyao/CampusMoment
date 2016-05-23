package provider;

import android.content.Context;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.umeng.comm.core.utils.DeviceUtils;
import com.uy.bbs.R;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by Shine on 2016/3/11.
 */
public class NetWorkPrvdr {
    public static final String baseUrl = "http://123.56.254.154/testAPI/public/?s=/test/Index/";
    public static final String register = baseUrl + "register";
    public static final String login = baseUrl + "login";
    public static OkHttpClient httpClient;
    public static String updateUser = baseUrl + "updateUser";
    public final Context context;

    public NetWorkPrvdr(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(5, TimeUnit.MINUTES);
        httpClient = builder.build();
        this.context = context;
    }

    public void post(String url, Map<String, String> params, Callback responseCallback) {
        try {
            if (!DeviceUtils.isNetworkAvailable(context)) {
                ToastUtil.showLongToast(context, R.string.network_error);
                responseCallback.onFailure(null, null);
                return;
            }

            FormBody.Builder formBody = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
            FormBody body = formBody.build();
            Log.i("http-request", LoganSquare.serialize(params));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(responseCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
