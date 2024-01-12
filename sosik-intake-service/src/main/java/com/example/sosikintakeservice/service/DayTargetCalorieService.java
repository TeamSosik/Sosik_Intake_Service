package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetDayTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

public interface DayTargetCalorieService {
    RequestTargetCalorie createTargetCalorie(Long memberId, RequestTargetCalorie requestTargetCalorie);

    String updateDayTargetCalorie(UpdateTargetCalorie updateTargetCalorie);

    ResponseGetDayTargetCalorie getDayTargetCalorie(Long memberId, String today);
}
