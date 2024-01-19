package com.example.sosikintakeservice.controller;


import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestUpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;
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

    @PostMapping("/v1/")
    public Result<Void> createTargetCalorie(@RequestHeader Long memberId,
                                            @RequestBody @Valid final RequestTargetCalorie requestTargetCalorie) {
        dayTargetCalorieService.createTargetCalorie(memberId,requestTargetCalorie);
        return Result.success();
    }

    @PatchMapping("/v1/")
    public Result<Void> updateTargetCalorie(@RequestHeader Long memberId ,
                                            @RequestBody @Valid final RequestUpdateTargetCalorie requestUpdateTargetCalorie) {
        dayTargetCalorieService.updateDayTargetCalorie(memberId, requestUpdateTargetCalorie);
        return Result.success();
    }

    @GetMapping("/v1/{today}")
    public Result<ResponseGetDayTargetCalorie> getTargetCalorie(@RequestHeader Long memberId, @PathVariable String today) {
        ResponseGetDayTargetCalorie dayTargetCalorie = dayTargetCalorieService.getDayTargetCalorie(memberId,today);
        if(dayTargetCalorie==null){
            return Result.success(null);
        }
        else{
            return Result.success(dayTargetCalorie);
        }
    }
}
