package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import com.example.sosikintakeservice.dto.response.ResponseGetDayTargetCalorie;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.model.entity.DayTargetCalorieEntity;
import com.example.sosikintakeservice.repository.TargetCalorieRepository;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class DayTargetCalorieServiceImplTest {
    private final DayTargetCalorieEntity dayTargetCalorieEntity = testTargetCalorie();
    @InjectMocks
    private DayTargetCalorieServiceImpl dayTargetCalorieService;
    @Mock
    private TargetCalorieRepository targetCalorieRepository;
    @Mock
    private Optional<DayTargetCalorieEntity> targetCalorieEntity = Optional.of(testTargetCalorie());

    private ResponseGetDayTargetCalorie expectedResponse = testGetDayTargetCalorieDTO();
    @Mock
    private LocalDateTime currentTime = LocalDateTime.now();
    @Mock
    private LocalDateTime lastCreatedDate;


    @DisplayName("일일목표칼로리 기입에 성공한다.")
    @Test
    void givenTestTargetCalorieWhenCreateTargetCalorieThenSuccess() {

        RequestTargetCalorie testTargetCalorieDto = testTargetCalorieDto();

        given(targetCalorieRepository.save(any())).willReturn(any());

        assertThat(dayTargetCalorieService.createTargetCalorie(testTargetCalorieDto)).isEqualTo(testTargetCalorieDto);

    }

    @DisplayName("일일목표칼로리 기입에 실패한다. - 오늘 등록했음")
    @Test
    void givenTestTargetCalorieWhenCreateTargetCalorieThrowEXISTENCE_TARGETCALORIE_ERROR() {

        RequestTargetCalorie testTargetCalorieDto = testTargetCalorieErrorDto();
        given(targetCalorieRepository.findTopByOrderByCreatedAtDesc()).willReturn(targetCalorieEntity);
        when(targetCalorieEntity.get().getCreatedAt()).thenReturn(currentTime);
        given(lastCreatedDate).willReturn(LocalDateTime.now());
        assertThatThrownBy(() -> dayTargetCalorieService.createTargetCalorie(testTargetCalorieDto))
                .isInstanceOf(ApplicationException.class);
    }

    @DisplayName("일일목표칼로리 수정에 성공한다.")
    @Test
    void givenTestTargetCalorieWhenUpdateTargetCalorieThenSuccess() {
        UpdateTargetCalorie updateTargetCalorie = testUpdateTargetCalorieDto();
        given(targetCalorieRepository.findById(updateTargetCalorie.Id())).willReturn(Optional.ofNullable(dayTargetCalorieEntity));
        dayTargetCalorieEntity.updateTargetCalorie(updateTargetCalorie);
        assertThat(dayTargetCalorieService.updateDayTargetCalorie(updateTargetCalorie)).isEqualTo("ok");

    }

    @DisplayName("일일목표칼로리 수정에 실패한다. - updateDTO id로 찾는 entity가 없을 때")
    @Test
    void givenTestTargetCalorieWhenUpdateTargetCalorieThrowTARGETCALORIE_NOT_FOUND() {
        UpdateTargetCalorie updateTargetCalorie = testUpdateTargetCalorieErrorDto();
        given(targetCalorieRepository.findById(updateTargetCalorie.Id())).willReturn(Optional.empty());
        assertThatThrownBy(() -> dayTargetCalorieService.updateDayTargetCalorie(updateTargetCalorie))
                .isInstanceOf(ApplicationException.class);

    }

    private static RequestTargetCalorie testTargetCalorieDto() {
        return RequestTargetCalorie.builder()
                .memberId(1L)
                .dayTargetKcal(2000)
                .dailyIntakePurpose(1)
                .build();
    }

    private static RequestTargetCalorie testTargetCalorieErrorDto() {
        return RequestTargetCalorie.builder()
                .memberId(1L)
                .dailyIntakePurpose(1)
                .build();
    }

    private static UpdateTargetCalorie testUpdateTargetCalorieDto() {
        return UpdateTargetCalorie.builder()
                .Id(1L)
                .dayTargetKcal(5000)
                .dailyIntakePurpose(1)
                .build();
    }

    private static UpdateTargetCalorie testUpdateTargetCalorieErrorDto() {
        return UpdateTargetCalorie.builder()
                .Id(1L)
                .dailyIntakePurpose(1)
                .build();
    }

    private static DayTargetCalorieEntity testTargetCalorie() {
        return DayTargetCalorieEntity.builder()
                .memberId(1L)
                .dayTargetKcal(2000)
                .dailyIntakePurpose(1)
                .build();
    }

    @DisplayName("일일 목표 칼로리 조회에 성공하였습니다.")
    @Test
    void givenTestDayTargetCalorieWhenGetDayTargetCalorieThenSuccess() {
        ResponseGetDayTargetCalorie getDayTargetCalorie = testGetDayTargetCalorieDTO();
        assertThat(getDayTargetCalorie).isEqualTo(expectedResponse);
    }

    private static ResponseGetDayTargetCalorie testGetDayTargetCalorieDTO() {
        return ResponseGetDayTargetCalorie.builder()
                .memberId(1L)
                .dayTargetKcal(1000)
                .dailyIntakePurpose(2000)
                .build();
    }
}