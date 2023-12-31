package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetDayTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.DayTargetCalorieEntity;
import com.example.sosikintakeservice.repository.TargetCalorieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DayTargetCalorieServiceImpl implements DayTargetCalorieService {
    private final TargetCalorieRepository targetCalorieRepository;

    @Override
    public RequestTargetCalorie createTargetCalorie(RequestTargetCalorie requestTargetCalorie) {
        Optional<DayTargetCalorieEntity> entity = targetCalorieRepository.findTopByOrderByLastCreatedDateDesc();
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime lastCreatedDate = entity.get().getCreatedAt();
        if (entity.isEmpty() || (currentTime.getDayOfMonth() != lastCreatedDate.getDayOfMonth() &&
                currentTime.getMonth() != lastCreatedDate.getMonth() &&
                currentTime.getYear() != lastCreatedDate.getYear())) {  //오늘 기록을 안한 경우
            DayTargetCalorieEntity dayTargetCalorieEntity = DayTargetCalorieEntity.builder()
                    .memberId(requestTargetCalorie.memberId())
                    .dayTargetKcal(requestTargetCalorie.dayTargetKcal())
                    .dailyIntakePurpose(requestTargetCalorie.dailyIntakePurpose())
                    .build();
            targetCalorieRepository.save(dayTargetCalorieEntity);
        } else { //오늘 목표칼로리가 이미 입력했다면 에러발생
            throw new ApplicationException(ErrorCode.EXISTENCE_TARGETCALORIE_ERROR);
        }
        return requestTargetCalorie;
    }

    @Override
    public String updateDayTargetCalorie(UpdateTargetCalorie updateTargetCalorie) {
        DayTargetCalorieEntity dayTargetCalorieEntity = targetCalorieRepository.findById(updateTargetCalorie.Id()).orElseThrow(() -> {
            return new ApplicationException(ErrorCode.TARGETCALORIE_NOT_FOUND);
        });
        dayTargetCalorieEntity.updateTargetCalorie(updateTargetCalorie);
        return "ok";
    }

    @Override
    public List<ResponseGetDayTargetCalorie> getDayTargetCalorie(RequestGetDayTargetCalorie getDayTargetCalorie) {
        List<DayTargetCalorieEntity> dayTargetCalorieEntityList = targetCalorieRepository.findByMemberIdAndCreateAt(getDayTargetCalorie.memberId(), getDayTargetCalorie.createdAt());
        return dayTargetCalorieEntityList.stream()
                .map(dayTargetCalorieEntity -> {
                    return ResponseGetDayTargetCalorie.builder()
                            .memberId(dayTargetCalorieEntity.getMemberId())
                            .dayTargetKcal(dayTargetCalorieEntity.getDayTargetKcal())
                            .dailyIntakePurpose(dayTargetCalorieEntity.getDailyIntakePurpose())
                            .build();
                }).collect(Collectors.toList());
    }
}
