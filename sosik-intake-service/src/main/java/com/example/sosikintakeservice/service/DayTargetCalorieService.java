package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;

public interface DayTargetCalorieService {
    RequestTargetCalorie createTargetCalorie(Long memberId, RequestTargetCalorie requestTargetCalorie);

    String updateDayTargetCalorie(UpdateTargetCalorie updateTargetCalorie);

    ResponseGetDayTargetCalorie getDayTargetCalorie(Long memberId, String today);
}
