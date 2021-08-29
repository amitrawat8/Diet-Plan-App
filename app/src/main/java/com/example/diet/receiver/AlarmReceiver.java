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


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.example.diet.R;
import com.example.diet.constant.BaseConstant;
import com.example.diet.entity.DietDataList;
import com.example.diet.roomDB.DietDataDatabase;
import com.example.diet.view.ui.MainActivity;

import java.util.Calendar;

import static android.os.Build.VERSION.SDK_INT;


public class AlarmReceiver extends WakefulBroadcastReceiver {

    public void setAlarm(Context context, Calendar calendar, int ID, String title) {
        Log.e("setAlarm", String.valueOf(ID));
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(BaseConstant.EXTRA_REMINDER_ID, Integer.toString(ID));
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;
    /*    mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                0, mPendingIntent);*/
        // Start alarm using notification time
        long time = SystemClock.elapsedRealtime() + diffTime;
    /*    if (Build.VERSION.SDK_INT >= 23) {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME,
                    time, mPendingIntent);
        } else
        if (Build.VERSION.SDK_INT >= 19) {
            mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME, time, mPendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, time, mPendingIntent);
        }
*/

        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                time,
                mPendingIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime, String title) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(BaseConstant.EXTRA_REMINDER_ID, Integer.toString(ID));
        intent.putExtra(BaseConstant.EXTRA_REMINDER_TITLE, title);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification timein
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Start alarm using initial notification time and repeat interval time
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                RepeatTime, mPendingIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int ID) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        if (mPendingIntent == null) {
            return;
        }
        mAlarmManager.cancel(mPendingIntent);

        // Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

     //   Log.d("TAG", "Intent action:: " + mReceivedID);
        if (intent != null) {
            int mReceivedID = Integer.parseInt(intent.getStringExtra(BaseConstant.EXTRA_REMINDER_ID));
       //     Log.d("TAG", "Intent action:: " + intent.getAction());

            //  int mReceivedID = Integer.parseInt(intent.getStringExtra(BaseConstant.EXTRA_REMINDER_ID));
            // String mTitle = intent.getStringExtra(BaseConstant.EXTRA_REMINDER_TITLE);
         //   Log.e("receive", "onreceive");
            //Get notification title from Reminder Database
            DietDataDatabase dietDataDatabase = DietDataDatabase.getInstance(context);
            if (dietDataDatabase != null && dietDataDatabase.getDataDao() != null) {
                DietDataList dietDataList = dietDataDatabase.getDataDao().getDietById(mReceivedID);
                if (dietDataList == null || dietDataList.getFood() == null) {
                 //   Log.e("null2", "null");
                    return;
                }
                if (dietDataList.getReceiveCount() != null && dietDataList.getReceiveCount().equals(BaseConstant.COUNT_0)) {
                    dietDataDatabase.getDataDao().updateCount(BaseConstant.COUNT_1, mReceivedID);
                  //  Log.e("null3", "null");
                    return;
                }
            //    Log.e("null4", "null");
                String mTitle = dietDataList.getFood();
                // Create intent to open ReminderEditActivity on notification click

                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Intent editIntent = new Intent(context, MainActivity.class);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                String NOTIFICATION_CHANNEL_ID = String.valueOf(mReceivedID);

                if (SDK_INT >= Build.VERSION_CODES.O) {
                    @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

                    //Configure Notification Channel
                    notificationChannel.setDescription("Diet Plan");
                    notificationChannel.enableLights(true);
                    notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    notificationChannel.enableVibration(true);

                    notificationManager.createNotificationChannel(notificationChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.diet_icon_launcher))
                                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                                .setContentTitle(mTitle)
                                .setTicker(mTitle)
                                .setAutoCancel(true)
                                .setSound(defaultSound)
                                .setContentIntent(pendingIntent)
                                /* .setStyle(style)*/
                                .setWhen(System.currentTimeMillis())
                                .setPriority(Notification.PRIORITY_HIGH);

                notificationManager.notify(mReceivedID, notificationBuilder.build());

            }


        }
    }
}