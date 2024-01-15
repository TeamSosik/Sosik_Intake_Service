package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;

import java.time.LocalDate;
import java.util.List;

public interface IntakeService {
    String createIntake(RequestIntake intakeDTO);
    List<ResponseGetIntake> getIntakes(Long memberId, LocalDate createdAt);
    String deleteIntake(Long intakeId);

}
