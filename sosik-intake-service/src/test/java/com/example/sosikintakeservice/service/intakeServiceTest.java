package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetFoodInfo;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.model.entity.Category;
import com.example.sosikintakeservice.repository.IntakeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class intakeServiceTest {

    @InjectMocks
    private IntakeService intakeService;
    @Mock
    private IntakeRepository intakeRepository;
    private ResponseGetIntake expectedResponse = testgetIntakeDTO();
    private static Category category;

    @DisplayName("섭취 음식 생성시 정상적으로 작동된다.")
    @Test
    void givenTestIntakeWhenCreateIntakeThenSuccess() {
        RequestIntake testIntakeDTO = testIntakeDTO();
        given(intakeRepository.save(any())).willReturn(any());
        assertThat(intakeService.createIntake(testIntakeDTO)).isEqualTo("ok");
    }

    @DisplayName("섭취 음식 생성시 정상적으로 계산되어 작동된다.")
    @Test
    void givenTestIntakeWhenCreateIntakeThenSuccess2() {
        RequestGetFoodInfo foodInfo = new RequestGetFoodInfo("300", "40", "20", "10", "25");

        BigDecimal total = new BigDecimal(foodInfo.total());
        BigDecimal calculationKcal = new BigDecimal(foodInfo.kcal()).multiply(total.divide(new BigDecimal("100")));
        BigDecimal calculationCarbo = new BigDecimal(foodInfo.carbo()).multiply(total.divide(new BigDecimal("100")));
        BigDecimal calculationProtein = new BigDecimal(foodInfo.protein()).multiply(total.divide(new BigDecimal("100")));
        BigDecimal calculationFat = new BigDecimal(foodInfo.fat()).multiply(total.divide(new BigDecimal("100")));

        RequestIntake testIntakeDTO1 = RequestIntake.builder()
                .memberId(1L)
                .foodId(2L)
                .dayTargetCalorieId(3L)
                .calculationCarbo(calculationCarbo)
                .calculationFat(calculationFat)
                .calculationKcal(calculationKcal)
                .calculationProtein(calculationProtein)
                .category(category)
                .foodAmount(150)
                .build();
        System.out.println(testIntakeDTO1);

        given(intakeRepository.save(any())).willReturn(any());
        assertThat(intakeService.createIntake(testIntakeDTO1)).isEqualTo("ok");
    }

    @DisplayName("섭취 음식 조회시 정상적으로 작동된다.")
    @Test
    void givenTestIntakeWhenGetIntakeThenSuccess() {
        ResponseGetIntake actualResponse = testgetIntakeDTO();
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("섭취 음식 삭제시 정상적으로 작동된다.")
    @Test
    void givenTestMemberWhenDeleteMemberThenSuccess(){
        String ok = intakeService.deleteIntake(1L);
        assertThat(ok).isEqualTo("ok");
    }

    @DisplayName("섭취 음식 삭제시 존재하지 않는다.")
    @Test
    void givenTestMemberWhenDeleteMemberThrowINTAKE_NOT_FOUND(){
        given(intakeRepository.findById(any())).willReturn(null);
        assertThatThrownBy(()-> intakeService.deleteIntake(1L)).isInstanceOf(ApplicationException.class);
    }


    private static RequestIntake testIntakeDTO() {
        return RequestIntake.builder()
                .memberId(1L)
                .foodId(2L)
                .dayTargetCalorieId(3L)
                .calculationCarbo(new BigDecimal("50.5"))
                .calculationFat(new BigDecimal("20.0"))
                .calculationKcal(new BigDecimal("400.0"))
                .calculationProtein(new BigDecimal("30.0"))
                .category(category)
                .foodAmount(150)
                .build();
    }

    private static ResponseGetIntake testgetIntakeDTO() {
        return ResponseGetIntake.builder()
                .memberId(1L)
                .foodId(2L)
                .dayTargetCalorieId(3L)
                .calculationCarbo(new BigDecimal("50.5"))
                .calculationFat(new BigDecimal("20.0"))
                .calculationKcal(new BigDecimal("400.0"))
                .calculationProtein(new BigDecimal("30.0"))
                .category(category)
                .foodAmount(150)
                .build();
    }


}
