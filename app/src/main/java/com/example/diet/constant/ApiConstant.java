package com.example.diet.constant;

import android.content.Context;


public class ApiConstant {
    public String API;
    private static com.example.diet.constant.ApiConstant sInstance;

    public ApiConstant(Context context) {
        API = BaseConstant.BASE_URL;
    }

    public static com.example.diet.constant.ApiConstant getsInstance(Context context) {
        synchronized (com.example.diet.constant.ApiConstant.class) {
            if (sInstance == null) {
                sInstance = new com.example.diet.constant.ApiConstant(context);
            }
        }
        return sInstance;
    }
}
