package com.example.sosikintakeservice.controller;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetCreateAt;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.model.entity.Category;
import com.example.sosikintakeservice.service.IntakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/intake/v1")
public class IntakeController {

    private final IntakeService intakeService;

    @PostMapping("/food")
    public Result<Void> createIntake(@RequestHeader Long memberId, @RequestBody RequestIntake intake) {
        intakeService.createIntake(memberId, intake);
        return Result.success();
    }

    @GetMapping("/{createdAt}")
    public Result<List<ResponseGetIntake>> getIntakes(@RequestHeader Long memberId, @PathVariable final LocalDate createdAt) {
        List<ResponseGetIntake> responseGetIntakes = intakeService.getIntakes(memberId,createdAt);
        return Result.success(responseGetIntakes);
    }

    @GetMapping("/check")
    public Result<List<ResponseGetCreateAt>> getIntakes(@RequestHeader Long memberId) {
        List<ResponseGetCreateAt> responseGetCreateAts = intakeService.getCreatedAtList(memberId);
        return Result.success(responseGetCreateAts);
    }

    @DeleteMapping("/{intakeId}")
    public Result<Void> deleteIntake(@PathVariable final Long intakeId) {
        intakeService.deleteIntake(intakeId);
        return Result.success();
    }

    @GetMapping("/rank")
    public Result<List<ResponseGetIntakeRank>> getRankList(@RequestHeader Long memberId,
                                 @RequestParam(defaultValue = "30") int period) {

        List<ResponseGetIntakeRank> body = intakeService.getRankList(memberId, period);

        return Result.success(body);
    }

}
