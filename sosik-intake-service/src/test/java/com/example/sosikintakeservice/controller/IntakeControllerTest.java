package com.example.sosikintakeservice.controller;

import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.service.IntakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

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
    void givenWhenThen() throws Exception {

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

    @DisplayName("섭취랭크조회성공_기간이없으면_기간은30이기본이다.")
    @Test
    void givenWhenNothingParamOfPeriodThenDefaultPeriod30() throws Exception {

        //given
        String url = "/intake/v1/rank";

        // when
        mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", 1L)
        );

        // then
        Mockito.verify(intakeService, Mockito.times(1)).getRankList(1L, 30);
    }

    @DisplayName("섭취랭크조회성공_조회데이터가없을때")
    @Test
    void givenEmptyListWhenGetIntakeRankThenSize0() throws Exception {

        //given
        Long memberId = 1L;
        int periodId = 7;
        String url = "/intake/v1/rank";
        Mockito.doReturn(Collections.emptyList())
                .when(intakeService)
                .getRankList(memberId, periodId);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", memberId)
                        .param("period", String.valueOf(periodId))
        );

        // then
        Mockito.verify(intakeService, Mockito.times(1)).getRankList(1L, 7);
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
        Mockito.doReturn(
                        List.of(
                                ResponseGetIntakeRank.builder()
                                        .intakeId(1L)
                                        .build(),
                                ResponseGetIntakeRank.builder()
                                        .intakeId(2L)
                                        .build()
                        )
                )
                .when(intakeService)
                .getRankList(memberId, periodId);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId", memberId)
                        .param("period", String.valueOf(periodId))
        );

        // then
        Mockito.verify(intakeService, Mockito.times(1)).getRankList(1L, 7);
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.result.size()").value(2));
    }




}
