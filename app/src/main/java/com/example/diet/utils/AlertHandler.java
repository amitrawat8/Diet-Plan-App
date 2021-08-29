package com.example.diet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.UiThread;
import com.example.diet.R;


public class AlertHandler {
    private static com.example.diet.utils.AlertHandler sInstance;
    Button okBtnTV;
    private Dialog alertDialog = null;
    private TextView msgTV;
    private ImageView iconTV;
    private ProgressBar ok_progress_bar;
    private AlertHandler() {

    }

    public static com.example.diet.utils.AlertHandler getInstance() {
        synchronized (com.example.diet.utils.AlertHandler.class) {
            if (sInstance == null) {
                sInstance = new com.example.diet.utils.AlertHandler();
            }
        }
        return sInstance;
    }

    private void initAlertDialog(Activity activity) {
        alertDialog = new Dialog(activity);
        alertDialog.setContentView(R.layout.dialog_ok_cancel_alert);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void showLongToast(Context context, String s) {
        if (context != null) {
            Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @UiThread
    public void showAlertDialog(final Activity activity, String message, int icon) {
        if (activity == null || activity.isFinishing()) return;

        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }

        initAlertDialog(activity);
        okBtnTV = alertDialog.findViewById(R.id.ok_btn);
        msgTV = alertDialog.findViewById(R.id.msg_tv);
        iconTV = alertDialog.findViewById(R.id.icon_tv);
        msgTV.setText(message);
        okBtnTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.dismissDialogSafely(activity, alertDialog);
            }
        });
        if (ok_progress_bar != null && okBtnTV != null) {
            CommonUtil.loaderViewHandler(ok_progress_bar, okBtnTV);
        }
        CommonUtil.showDialogSafely(activity, alertDialog);
    }
}