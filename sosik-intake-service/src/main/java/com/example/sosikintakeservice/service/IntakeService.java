package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.IntakeRankCondition;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetCreateAt;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;

import java.time.LocalDate;
import java.util.List;

public interface IntakeService {
    String createIntake(Long memberId, RequestIntake intakeDTO);
    List<ResponseGetIntake> getIntakes(Long memberId, LocalDate createdAt);
    String deleteIntake(Long intakeId);
    List<ResponseGetCreateAt> getCreatedAtList(Long memberId);
    List<ResponseGetIntakeRank> getRankList(IntakeRankCondition intakeRankCondition, Long memberId, int period);
}
