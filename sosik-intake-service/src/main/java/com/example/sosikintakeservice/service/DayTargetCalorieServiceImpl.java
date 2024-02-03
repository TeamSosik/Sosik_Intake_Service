package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestUpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.DayTargetCalorieEntity;
import com.example.sosikintakeservice.repository.TargetCalorieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DayTargetCalorieServiceImpl implements DayTargetCalorieService {
    private final TargetCalorieRepository targetCalorieRepository;

    @Override
    public RequestTargetCalorie createTargetCalorie(Long memberId , RequestTargetCalorie requestTargetCalorie) {
        LocalDate currentTime = LocalDate.now();
        DayTargetCalorieEntity entity = targetCalorieRepository
                .findByMemberIdAndCreatedAt(memberId,currentTime).orElse(null);
        if (Objects.nonNull(entity)) {  //오늘 기록했다면
            throw new ApplicationException(ErrorCode.EXISTENCE_TARGETCALORIE_ERROR);
        } else { //오늘 기록 안했다면
            DayTargetCalorieEntity dayTargetCalorieEntity = DayTargetCalorieEntity
                    .buildDayTargetCalorie(memberId,requestTargetCalorie);
            targetCalorieRepository.save(dayTargetCalorieEntity);
        }
        return requestTargetCalorie;
    }

    @Override
    public void updateDayTargetCalorie(Long memberId, RequestUpdateTargetCalorie requestUpdateTargetCalorie) {
        DayTargetCalorieEntity dayTargetCalorieEntity = targetCalorieRepository
                .findByMemberIdAndCreatedAt(memberId, requestUpdateTargetCalorie.createdAt()).orElseThrow(() -> {
            return new ApplicationException(ErrorCode.TARGETCALORIE_NOT_FOUND);
        });
        dayTargetCalorieEntity.updateTargetCalorie(requestUpdateTargetCalorie);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseGetDayTargetCalorie getDayTargetCalorie(Long memberId, String today) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(today, inputFormatter);
        DayTargetCalorieEntity dayTargetCalorieEntity = targetCalorieRepository
                .findByMemberIdAndCreatedAt(memberId, localDate).orElse(null);
        if (Objects.isNull(dayTargetCalorieEntity)){
            ResponseGetDayTargetCalorie responseGetDayTargetCalorie = null;
            return responseGetDayTargetCalorie;
        }
        else {
            ResponseGetDayTargetCalorie responseGetDayTargetCalorie = ResponseGetDayTargetCalorie
                    .buildResponseGetDayTargetCalorie(dayTargetCalorieEntity);
            return responseGetDayTargetCalorie;
        }
    }
}
