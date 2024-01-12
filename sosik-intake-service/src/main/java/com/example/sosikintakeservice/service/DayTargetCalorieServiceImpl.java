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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DayTargetCalorieServiceImpl implements DayTargetCalorieService {
    private final TargetCalorieRepository targetCalorieRepository;

    @Override
    public RequestTargetCalorie createTargetCalorie(Long memberId , RequestTargetCalorie requestTargetCalorie) {
        LocalDate currentTime = LocalDate.now();
        Optional<DayTargetCalorieEntity> entity = targetCalorieRepository.findByMemberIdAndCreatedAt(memberId,currentTime);
        if (!entity.isEmpty()) {  //오늘 기록했다면
            throw new ApplicationException(ErrorCode.EXISTENCE_TARGETCALORIE_ERROR);
           
        } else { //오늘 기록 안했다면
            DayTargetCalorieEntity dayTargetCalorieEntity = DayTargetCalorieEntity.builder()
                    .memberId(memberId)
                    .dayTargetKcal(requestTargetCalorie.dayTargetKcal())
                    .dailyIntakePurpose(requestTargetCalorie.dailyIntakePurpose())
                    .build();
            targetCalorieRepository.save(dayTargetCalorieEntity);
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
    public ResponseGetDayTargetCalorie getDayTargetCalorie(Long memberId, String today) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(today, inputFormatter);

        Optional<DayTargetCalorieEntity> dayTargetCalorieEntity = targetCalorieRepository
                .findByMemberIdAndCreatedAt(memberId, localDate);
//        System.out.println(localDate.atStartOfDay());
//        if(dayTargetCalorieEntity.isEmpty()){
//            throw new ApplicationException(ErrorCode.TARGETCALORIE_NOT_FOUND);
//        }
        System.out.println(localDate);
        System.out.println(dayTargetCalorieEntity);
        ResponseGetDayTargetCalorie responseGetDayTargetCalorie = ResponseGetDayTargetCalorie.builder()
                .dayTargetKcal(dayTargetCalorieEntity.get().getDayTargetKcal())
                .dailyIntakePurpose(dayTargetCalorieEntity.get().getDailyIntakePurpose())
                .build();
        return responseGetDayTargetCalorie;
    }
}
