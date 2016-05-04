package helper.common_util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Created by ljy on 15/12/11.
 */
public class AlertDialogUtils {
    private static AlertDialog alertDialog = null;

    public static Builder builder(Context context) {
        Builder builder = new Builder(context);
        return builder;
    }

    public static void dismiss() {
        alertDialog.dismiss();
    }

    public static class Builder {
        private Context mContext;
        private String title = "";
        private String content = "";

        private String strPositive = "确定";
        private String strNegative = "取消";

        private boolean isCanceledOnTouchOutside = true;

        private AlertDialog.OnClickListener onPositiveClickListener;
        private AlertDialog.OnClickListener onNegativeClickListener;
        private View.OnClickListener onDismissListener;

        private boolean cancelable = true;


        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(String value) {
            title = value;
            return this;
        }

        public Builder setContent(String value) {
            content = value;
            return this;
        }

        public Builder setOnPositiveClickListener(AlertDialog.OnClickListener value) {
            onPositiveClickListener = value;
            return this;
        }

        public Builder setOnNegativeClickListener(AlertDialog.OnClickListener value) {
            onNegativeClickListener = value;
            return this;
        }

        public Builder setOnDismissListener(View.OnClickListener value) {
            onDismissListener = value;
            return this;
        }

        public Builder setStrPositive(String value) {
            strPositive = value;
            return this;
        }

        public Builder setStrNegative(String value) {
            strNegative = value;
            return this;
        }

        public Builder setIsCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
            this.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder show() {
            if (onNegativeClickListener == null) {
                onNegativeClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();

                    }
                };
            }
            if (onPositiveClickListener == null) {
                onPositiveClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                };
            }
            alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(strPositive, onPositiveClickListener)
                    .setNegativeButton(strNegative, onNegativeClickListener)
                    .create();
            alertDialog.setCancelable(cancelable);
            alertDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
            alertDialog.show();
            return this;
        }
    }
}
