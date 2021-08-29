/**
 * Created by Amit Rawat on 28-08-2021
 */

package com.example.diet.dto;

import lombok.Data;

@Data
public class DietRoot {
    private int diet_duration;

    private WeekDietData week_diet_data;
}
