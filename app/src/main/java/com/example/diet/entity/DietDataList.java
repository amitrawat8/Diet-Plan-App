package com.example.diet.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(tableName = "DietDataList")
public class DietDataList implements Serializable {
    @PrimaryKey(autoGenerate = false)
    private int dietID;

    @ColumnInfo(name = "week")
    private String week;

    @ColumnInfo(name = "food")
    private String food;

    @ColumnInfo(name = "meal_time")
    private String mealTime;

    @ColumnInfo(name = "m_active")
    private String mActive;

    @ColumnInfo(name = "m_repeat")
    private String mRepeat;

    @ColumnInfo(name = "receiveCount")
    private String receiveCount = "0";

    @ColumnInfo(name = "offline")
    private String offline ;


}
