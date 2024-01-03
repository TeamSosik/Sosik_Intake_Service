package com.example.sosikintakeservice.controller;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.service.IntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/intake")
public class IntakeController {

    private final IntakeService intakeService;

    @PostMapping("/food")
    public Result<Void> createIntake(@RequestBody RequestIntake intake) {
        intakeService.createIntake(intake);
        return Result.success();
    }

    @GetMapping("/{intakeId}")
    public Result<ResponseGetIntake> getIntake(@PathVariable final Long intakeId) {
        ResponseGetIntake responseGetIntake = intakeService.getIntake(intakeId);
        return Result.success(responseGetIntake);
    }
    @GetMapping("/")
    public Result<List<ResponseGetIntake>> getIntakes(@RequestBody final RequestGetIntake requestgetIntake) {
        List<ResponseGetIntake> responseGetIntakes = intakeService.getIntakes(requestgetIntake);
        return Result.success(responseGetIntakes);
    }

    @DeleteMapping("/{intakeId}")
    public Result<Void> deleteIntake(@PathVariable final Long intakeId) {
        intakeService.deleteIntake(intakeId);
        return Result.success();
    }

}
