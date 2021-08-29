package com.example.diet.roomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.diet.constant.BaseConstant;
import com.example.diet.daoInterface.DietDataDao;
import com.example.diet.entity.DietDataList;

@Database(entities = {DietDataList.class}, version = 1, exportSchema = false)
public abstract class DietDataDatabase extends RoomDatabase {
    public abstract DietDataDao getDataDao();

    private static DietDataDatabase dietPlanDatabase;

    public static DietDataDatabase getInstance(Context context) {
        if (null == dietPlanDatabase) {
            dietPlanDatabase = buildDatabaseInstance(context);
        }
        return dietPlanDatabase;
    }

    private static DietDataDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                DietDataDatabase.class,
                BaseConstant.DB_NAME)
                .allowMainThreadQueries().build();
    }

}
