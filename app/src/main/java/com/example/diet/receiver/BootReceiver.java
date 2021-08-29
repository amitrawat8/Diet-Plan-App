/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.diet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.diet.constant.BaseConstant;
import com.example.diet.entity.DietDataList;
import com.example.diet.roomDB.DietDataDatabase;

import java.util.Calendar;
import java.util.List;


public class BootReceiver extends BroadcastReceiver {

    private String[] mTimeSplit;
    private int mHour, mMinute;

    private AlarmReceiver mAlarmReceiver;

    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            context = context;
            DietDataDatabase rb = DietDataDatabase.getInstance(context);
            mAlarmReceiver = new AlarmReceiver();
            List<DietDataList> reminders = rb.getDataDao().getALLDietData();
            for (DietDataList dietDataList : reminders) {
                setAlaram(dietDataList);
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
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);
        if (item.getMActive().equals("true")) {
            if (item.getMRepeat().equals("true")) {
                mAlarmReceiver.setRepeatAlarm(context, mCalendar, item.getDietID(), 4,item.getFood());
            } else if (item.getMRepeat().equals("false")) {
                mAlarmReceiver.setAlarm(context, mCalendar, item.getDietID(),item.getFood());
            }
        }
    }
}