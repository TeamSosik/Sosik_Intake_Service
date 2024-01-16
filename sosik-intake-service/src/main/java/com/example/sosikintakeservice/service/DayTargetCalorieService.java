package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestUpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;

public interface DayTargetCalorieService {
    RequestTargetCalorie createTargetCalorie(Long memberId, RequestTargetCalorie requestTargetCalorie);

    void updateDayTargetCalorie(Long memberId , RequestUpdateTargetCalorie requestUpdateTargetCalorie);

    ResponseGetDayTargetCalorie getDayTargetCalorie(Long memberId, String today);
}
