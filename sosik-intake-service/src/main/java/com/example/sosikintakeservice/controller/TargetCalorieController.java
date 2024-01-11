package com.example.sosikintakeservice.controller;


import com.example.sosikintakeservice.dto.request.RequestGetDayTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.service.DayTargetCalorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/target-calorie")
public class TargetCalorieController {
    private final DayTargetCalorieService dayTargetCalorieService;

//    @PostMapping("")
//    public Result<Void> createTargetCalorie(@RequestBody @Valid final RequestTargetCalorie requestTargetCalorie) {
//        dayTargetCalorieService.createTargetCalorie(requestTargetCalorie);
//        return Result.success();
//    }

    @PatchMapping("")
    public Result<Void> updateTargetCalorie(@RequestBody @Valid final UpdateTargetCalorie updateTargetCalorie) {
        dayTargetCalorieService.updateDayTargetCalorie(updateTargetCalorie);
        return Result.success();
    }

    @GetMapping("/v1/{today}")
    public Result<ResponseGetDayTargetCalorie> getTargetCalorie(@RequestHeader Long memberId, @PathVariable String today) {
        ResponseGetDayTargetCalorie dayTargetCalorie = dayTargetCalorieService.getDayTargetCalorie(memberId,today);
        System.out.println(today+memberId);
        return Result.success(dayTargetCalorie);
    }
}
