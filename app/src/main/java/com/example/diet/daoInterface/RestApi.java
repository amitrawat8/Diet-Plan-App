package com.example.diet.daoInterface;

import com.example.diet.constant.BaseConstant;
import com.example.diet.dto.DietRoot;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestApi {
    @GET(BaseConstant.GET_DIET)
    Call<DietRoot> getDietPlan();
}
