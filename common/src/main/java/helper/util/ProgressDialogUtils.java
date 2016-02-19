package helper.util;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * Created by shine on 16-2-17.
 */
public class ProgressDialogUtils {
    private ProgressDialog progressDialog;

    public ProgressDialogUtils(Context context) {
        progressDialog = new ProgressDialog(context);
    }

    public void show(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }
}
