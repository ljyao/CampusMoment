package provider;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Shine on 2016/3/11.
 */
public abstract class CallBackListener<T> {
    public void onComplete(int code, String reason, String response) {
    }

    public void onComplete(int code, String reason, T data) {
    }

    public void onResponse(Call call, Response response) throws Exception {
        String resultStr = response.body().string();
        Log.i("http", resultStr);
        JSONObject json = new JSONObject(resultStr);
        JSONObject returnData = new JSONObject();
        try {
            returnData = json.getJSONObject("returnData");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int code = json.getInt("code");
        String reason = json.getString("reason");
        onComplete(code, reason, returnData.toString());
    }

    public abstract void onFailure(Call call, Exception e);

}
