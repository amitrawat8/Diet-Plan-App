package com.example.diet.view.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diet.BuildConfig;
import com.example.diet.R;
import com.example.diet.adapter.DietPlanAdapter;
import com.example.diet.constant.BaseConstant;
import com.example.diet.dto.DietRoot;
import com.example.diet.dto.Monday;
import com.example.diet.dto.Thursday;
import com.example.diet.dto.Wednesday;
import com.example.diet.entity.DietDataList;
import com.example.diet.network.RetrofitClient;
import com.example.diet.receiver.AlarmReceiver;
import com.example.diet.roomDB.DietDataDatabase;
import com.example.diet.utils.CommonUtil;
import com.example.diet.utils.NetworkUtils;
import com.example.diet.view.ui.fragmentLoader.LoadingViewFragment;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietListFragment extends LoadingViewFragment implements View.OnClickListener {

    private RecyclerView dietRecyclerView;
    private Handler handler;
    private DietPlanAdapter dietPlanAdapter;
    private ArrayList<DietDataList> list = new ArrayList<>();
    private DietDataDatabase dietDataDatabase;
    private String[] mTimeSplit;
    private int mHour, mMinute;
    private int id = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getLoaderView(getContext(), inflater.inflate(R.layout
                .frag_diet_list, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            battery();
            layoutID(view);
            setAdapter();
            if (!NetworkUtils.isNetworkAvailable(getActivity())) {
                try {
                    checkData();
                } catch (Exception e) {
                    return;
                }
                return;
            }
            setHasOptionsMenu(true);
            loadVideoListData(false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkData() {
        if (dietDataDatabase != null
                && dietDataDatabase.getDataDao() != null
                && dietDataDatabase.getDataDao().getDietDto() != null) {
            dietPlanAdapter.clear();
         /*   for (DietDataList dietDataList : dietDataDatabase.getDataDao().getALLDietData()) {
                new AlarmReceiver().cancelAlarm(getActivity(), dietDataList.getDietID());
                setAlaram(dietDataList);
            }*/
            dietPlanAdapter.addAll(dietDataDatabase.getDataDao().getALLDietData());
            showContent();
        } else {
            showError(getString(R.string.err_msg_no_internet_connect));
        }
    }

    private void layoutID(View view) throws Exception {
        try {
            dietDataDatabase = DietDataDatabase.getInstance(getActivity());
            dietRecyclerView = view.findViewById(R.id.diet_list_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            dietRecyclerView.setLayoutManager(linearLayoutManager);
            retry_tv.setOnClickListener(this);
            if (handler == null) {
                handler = new Handler();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private void setAdapter() throws Exception {
        try {
            if (getActivity() != null) {
                dietPlanAdapter = new DietPlanAdapter(list, getActivity());
                dietRecyclerView.setAdapter(dietPlanAdapter);
            }

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private void loadVideoListData(boolean isRetry, boolean showprogress) throws Exception {

        try {

            if (showprogress) {
                showProgress();
            }

            if (handler == null) {
                handler = new Handler();
            }
            NetworkUtils.setNetworkMode(handler);
            if (!isRetry) {
                requestVideoListData(showprogress);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    private void requestVideoListData(boolean showprogress) throws Exception {
        try {
            if (showprogress) {
                showProgress();
            }

            Call<DietRoot> call = RetrofitClient.getInstance(getActivity()).getRestApi().getDietPlan();
            call.enqueue(new Callback<DietRoot>() {
                @Override
                public void onResponse(Call<DietRoot> call, Response<DietRoot> response) {
                    if (!CommonUtil.isUIThreadRunning(DietListFragment.this, getActivity())) {
                        return;
                    }

                    if (response != null && response.body() != null
                            && response.body().getWeek_diet_data() != null) {

                        try {
                            handleSuccessDiet(response.body(), showprogress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        showError();
                    }
                }

                @Override
                public void onFailure(Call<DietRoot> call, Throwable t) {
                    checkData();
                }
            });
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    private void handleSuccessDiet(DietRoot dietRoot, boolean showcontent) throws Exception {

        try {
            if (dietRoot == null || dietRoot.getWeek_diet_data() == null) {
                showError(getString(R.string.no_data_found));
            } else {
                dietDataDatabase.getDataDao().delete();
                if (dietRoot.getWeek_diet_data() != null) {
                    if (!CommonUtil.isNullOrEmpty(dietRoot.getWeek_diet_data().getMonday())) {
                        for (Monday monday : dietRoot.getWeek_diet_data().getMonday()) {
                            id = id + 1;

                            DietDataList dietDataList = getDietData(id, monday.getFood(),
                                    monday.getMeal_time(), BaseConstant.Monday);
                            dietDataDatabase.getDataDao().addData(dietDataList);

                        }
                    }
                    if (!CommonUtil.isNullOrEmpty(dietRoot.getWeek_diet_data().getThursday())) {
                        for (Thursday thursday : dietRoot.getWeek_diet_data().getThursday()) {
                            id = id + 1;
                            DietDataList dietDataList = getDietData(id, thursday.getFood(),
                                    thursday.getMeal_time(), BaseConstant.Thursday);
                            dietDataDatabase.getDataDao().addData(dietDataList);

                        }
                    }
                    if (!CommonUtil.isNullOrEmpty(dietRoot.getWeek_diet_data().getWednesday())) {
                        for (Wednesday wednesday : dietRoot.getWeek_diet_data().getWednesday()) {
                            id = id + 1;
                            DietDataList dietDataList = getDietData(id, wednesday.getFood(),
                                    wednesday.getMeal_time(), BaseConstant.Wednesday);

                            dietDataDatabase.getDataDao().addData(dietDataList);


                        }
                    }
                }

                dietPlanAdapter.clear();
                for (DietDataList dietDataList : dietDataDatabase.getDataDao().getALLDietData()) {
                    // new AlarmReceiver().cancelAlarm(getActivity(), dietDataList.getDietID());
                    setAlaram(dietDataList);
                }
                dietPlanAdapter.addAll(dietDataDatabase.getDataDao().getALLDietData());
                if (showcontent) {
                    showContent();
                }

            }
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    private DietDataList getDietData(int id, String food, String mealTime, String week) {
        DietDataList dietDataList = new DietDataList();
        dietDataList.setDietID(id);
        dietDataList.setFood(food);
        dietDataList.setMealTime(mealTime);
        dietDataList.setWeek(week);
        dietDataList.setMActive("true");
        dietDataList.setMRepeat("false");
        dietDataList.setReceiveCount(BaseConstant.COUNT_0);
        return dietDataList;
    }

    @Override
    public void onClick(View v) {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            try {

                checkData();
            } catch (Exception e) {
                return;
            }
            return;
        }
        if (v.getId() == R.id.retry_tv) {
            try {
                showProgress(LoadingViewFragment.PROGRESS_TYPE_TEXT_AND_CIRCLE, getString(R.string
                        .loading_msg), LoadingViewFragment.STYLE_WAVE);
                loadVideoListData(false, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setAlaram(DietDataList item) {
        mTimeSplit = item.getMealTime().split(":");
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);
        switch (item.getWeek()) {
            case BaseConstant.Monday:
                setalarm(2, mHour, mMinute, item);
                break;
            case BaseConstant.Tuesday:
                setalarm(3, mHour, mMinute, item);
                break;
            case BaseConstant.Wednesday:
                setalarm(4, mHour, mMinute, item);
                break;
            case BaseConstant.Thursday:
                setalarm(5, mHour, mMinute, item);
                break;
            case BaseConstant.Friday:
                setalarm(6, mHour, mMinute, item);
                break;
            case BaseConstant.Saturday:
                setalarm(7, mHour, mMinute, item);
                break;
            case BaseConstant.Sunday:
                setalarm(1, mHour, mMinute, item);
                break;

        }

    }

    public void setalarm(int weekno, int mHour, int mMinute, DietDataList item) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.DAY_OF_WEEK, weekno);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute - 5);
        mCalendar.set(Calendar.SECOND, 0);
        if (item.getMActive().equals("true")) {
            if (item.getMRepeat().equals("true")) {
                new AlarmReceiver().setRepeatAlarm(getActivity(), mCalendar, item.getDietID(), 4, item.getFood());
            } else if (item.getMRepeat().equals("false")) {
                new AlarmReceiver().setAlarm(getActivity(), mCalendar, item.getDietID(), item.getFood());
            }
        }
    }

    private void battery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = BuildConfig.APPLICATION_ID;
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }
}
