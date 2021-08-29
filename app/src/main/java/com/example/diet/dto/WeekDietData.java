/**
 * Created by Amit Rawat on 27-08-2021
 */

package com.example.diet.dto;

import java.util.List;

import lombok.Data;

@Data
public class WeekDietData {
    private List<Monday> monday;

    private List<Wednesday> wednesday;

    private List<Thursday> thursday;
}
