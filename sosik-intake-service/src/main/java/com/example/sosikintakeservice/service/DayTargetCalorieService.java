package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetDayTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;

import java.util.List;

public interface DayTargetCalorieService {
    RequestTargetCalorie createTargetCalorie(RequestTargetCalorie requestTargetCalorie);

    String updateDayTargetCalorie(UpdateTargetCalorie updateTargetCalorie);

    List<ResponseGetDayTargetCalorie> getDayTargetCalorie(RequestGetDayTargetCalorie getDayTargetCalorie);
}
