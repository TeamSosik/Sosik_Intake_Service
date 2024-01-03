package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;

import java.util.List;

public interface IntakeService {
    String createIntake(RequestIntake intakeDTO);
    ResponseGetIntake getIntake(Long intakeId);
    List<ResponseGetIntake> getIntakes(RequestGetIntake requestgetIntake);
    String deleteIntake(Long intakeId);

}
