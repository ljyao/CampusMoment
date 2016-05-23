package provider;

import android.content.Context;
import android.widget.Toast;

import com.uy.util.Worker;

/**
 * Created by Shine on 2016/3/13.
 */
public class ToastUtil {
    public static void showLongToast(final Context context, final String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    public static void showShortToast(final Context context, final int msgId) {
        showToast(context, msgId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(final Context context, final int msgId) {
        showToast(context, msgId, Toast.LENGTH_LONG);
    }

    public static void showShortToast(final Context context, final String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(final Context context, final String msg, final int duration) {
        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, duration).show();
            }
        });
    }

    private static void showToast(final Context context, final int msg, final int duration) {
        Worker.postMain(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, duration).show();
            }
        });
    }
}
