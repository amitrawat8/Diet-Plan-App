package com.example.diet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }
    public static void setNetworkMode(Handler handler) {
        if (handler != null) {
            DeviceBandwidthSampler.getInstance().startSampling();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DeviceBandwidthSampler.getInstance().stopSampling();
                }
            }, 15000);
        }
    }
}


