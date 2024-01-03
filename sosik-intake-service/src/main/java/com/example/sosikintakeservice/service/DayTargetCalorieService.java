package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;

public interface DayTargetCalorieService {
    RequestTargetCalorie createTargetCalorie(RequestTargetCalorie requestTargetCalorie);

    String updateDayTargetCalorie(UpdateTargetCalorie updateTargetCalorie);
}
