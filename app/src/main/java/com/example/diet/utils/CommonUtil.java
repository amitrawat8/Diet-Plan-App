package com.example.diet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.example.diet.constant.BaseConstant;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CommonUtil {

    public static boolean isNullOrEmpty(List<?> input) {
        return input == null || input.isEmpty();
    }
    public static boolean isUIThreadRunning(Activity activity) {
        return activity != null && !activity.isFinishing();
    }
    public static boolean isUIThreadRunning(Fragment fragment, Activity activity) {
        return activity != null && !activity.isFinishing() && fragment != null && fragment
                .isAdded();
    }
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty() || input.equalsIgnoreCase("null");
    }

    public static void dismissDialogSafely(Activity activity, Dialog dialog) {

        if (!activity.isFinishing() && dialog != null) {
            dialog.dismiss();
        }
    }

    public static void loaderViewHandler(View viewToHide, View viewToShow) {
        viewToHide.setVisibility(View.GONE);
        viewToShow.setVisibility(View.VISIBLE);
    }
    public static void showDialogSafely(Activity activity, Dialog dialog) {
        if (isUIThreadRunning(activity) && dialog != null) {
            dialog.show();
        }
        if (!activity.isFinishing() && dialog != null) {
            dialog.show();
        }
    }
    public static String getCurrDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(BaseConstant.DATE_FORMAT);
        return sdf.format(cal.getTime());
    }
}
