package com.example.sosikintakeservice.controller;

import com.example.sosikintakeservice.dto.IntakeRankCondition;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.service.IntakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class IntakeControllerTest {

    @InjectMocks
    IntakeController intakeController;
    @Mock
    IntakeService intakeService;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(intakeController)
                .setControllerAdvice(new IntakeControllerAdvice())
                .build();
    }

    @DisplayName("Mock객체중Null이없음")
    @Test
    void givenWhenThenNothingNullAboutMocks() {

        mockMvc = MockMvcBuilders.standaloneSetup(intakeController)
                        .build();

        assertThat(intakeController).isNotNull();
        assertThat(intakeService).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @DisplayName("섭취랭크조회실패_memberId가헤더에없음")
    @Test
    void givenUrlWhenNotExistingMemberIdThenThrowException() throws Exception {

        //given
        String url = "/intake/v1/rank";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("섭취랭크조회실패_랭크타입이없음")
    @Test
    void givenUrlAndMemberIdWhenNotExistingConditionThenThrowException() throws Exception {
        // given
        String url = "/intake/v1/rank";
        String memberId = "1";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", memberId)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("섭취랭크조회실패_랭크타입값이없음")
    @ParameterizedTest
    @MethodSource("getIntakeRankCondition")
    void givenUrlAndMemberIdWhenNotExistingConditionThenThrowException(
            String rankType
    ) throws Exception {
        // given
        String url = "/intake/v1/rank";
        String memberId = "1";

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", memberId)
                        .param("rankType", rankType)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("rankType"));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("값이 비어있을 수 없습니다."));
    }

    private static Stream<Arguments> getIntakeRankCondition() {

        return Stream.of(
                Arguments.of(""),
                Arguments.of("   ")
        );
    }

    @DisplayName("섭취랭크조회실패_서비스에서예외발생")
    @Test
    void givenWhenThen() throws Exception {

        // given
        String url = "/intake/v1/rank";
        String memberId = "1";
        String param = "food";
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                        .rankType(param)
                        .build();

        Mockito.doThrow(
                new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR)
        ).when(intakeService)
                .getRankList(Mockito.any(IntakeRankCondition.class), Mockito.any(Long.class), Mockito.any(Integer.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("memberId", memberId)
                .param("rankType", "food")
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isInternalServerError());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.name()));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("내부 서버의 오류입니다."));




    }


    @DisplayName("섭취랭크조회성공_기간이없으면_기간은30이기본이다.")
    @Test
    void givenWhenNothingParamOfPeriodThenDefaultPeriod30() throws Exception {

        //given
        String url = "/intake/v1/rank";
        String rankType = "kcal";
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("kcal")
                        .build();

        // when
        mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", 1L)
                        .param("rankType", rankType)
        );

        // then
        Mockito.verify(intakeService, Mockito.times(1)).getRankList(intakeRankCondition, 1L, 30);
    }

    @DisplayName("섭취랭크조회성공_조회데이터가없을때")
    @Test
    void givenEmptyListWhenGetIntakeRankThenSize0() throws Exception {

        //given
        Long memberId = 1L;
        int periodId = 7;
        String url = "/intake/v1/rank";
        String rankType = "food";
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
        Mockito.doReturn(Collections.emptyList())
                .when(intakeService)
                .getRankList(intakeRankCondition, memberId, periodId);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", memberId)
                        .param("period", String.valueOf(periodId))
                        .param("rankType", rankType)
        );

        // then
        Mockito.verify(intakeService, Mockito.times(1)).getRankList(intakeRankCondition, 1L, 7);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.result.size()").value(0));
    }

    @DisplayName("섭취랭크조회성공_조회데이터가2")
    @Test
    void givenEmptyListWhenGetIntakeRankThenSize2() throws Exception {

        //given
        Long memberId = 1L;
        int periodId = 7;
        String url = "/intake/v1/rank";
        String rankType = "kcal";
        Mockito.doReturn(
                        List.of(
                                ResponseGetIntakeRank.builder()
                                        .foodId(1L)
                                        .build(),
                                ResponseGetIntakeRank.builder()
                                        .foodId(2L)
                                        .build()
                        )
                )
                .when(intakeService)
                .getRankList(Mockito.any(IntakeRankCondition.class), Mockito.any(Long.class), Mockito.any(Integer.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", memberId)
                        .param("period", String.valueOf(periodId))
                        .param("rankType", rankType)
        );

        // then
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                        .rankType("kcal")
                        .build();

        Mockito.verify(intakeService, Mockito.times(1)).getRankList(intakeRankCondition, 1L, 7);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.result.size()").value(2));
    }




}
