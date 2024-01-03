package com.example.sosikintakeservice.controller;


import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.service.DayTargetCalorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/target-calorie")
public class TargetCalorieController {
    private final DayTargetCalorieService dayTargetCalorieService;
    @PostMapping("")
    public Result<Void> createTargetCalorie(@RequestBody @Valid final RequestTargetCalorie requestTargetCalorie){
        dayTargetCalorieService.createTargetCalorie(requestTargetCalorie);
        return Result.success();
    }


}
