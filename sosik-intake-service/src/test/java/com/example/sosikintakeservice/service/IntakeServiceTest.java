package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetFoodInfo;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.dto.response.redis.RedisFood;
import com.example.sosikintakeservice.dto.response.redis.RedisFoodRepository;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.model.entity.Category;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import com.example.sosikintakeservice.repository.IntakeRepository;
import com.example.sosikintakeservice.service.redis.RedisIntakeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class IntakeServiceTest {

    @InjectMocks
    private IntakeServiceImpl intakeService;
    @Mock
    private IntakeRepository intakeRepository;
    private ResponseGetIntake expectedResponse = testgetIntakeDTO();
    private static Category category;
    @Mock
    private RedisIntakeService redisIntakeService;
    @Mock
    private RedisFoodRepository redisFoodRepository;

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

    @DisplayName("db데이터죄회시_데이터가없으면_빈값을반환")
    @Test
    void givenEmptyArrayListWhenFindIntakeListThenSize0() {

        // given
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();

        Mockito.doReturn(new ArrayList<>())
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(memberId, start, end);

        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(memberId, period);

        // then
        assertThat(result.size()).isEqualTo(0);

    }

    @DisplayName("섭취목록조회시_데이터가있으면_레디스에저장_레디스에저장되지않은멤버")
    @Test
    void givenNotExistingIntakeRankWhenGetIntakeRankListThenIntakeRankList() {

        // given
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();

        Long foodId1 = 1L;
        Long foodId2 = 12L;
        Long foodId3 = 13L;

        Mockito.doReturn(
                        List.of(
                                IntakeEntity.builder().foodId(foodId1).build(),
                                IntakeEntity.builder().foodId(foodId2).build(),
                                IntakeEntity.builder().foodId(foodId3).build()
                        )
                )
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(memberId, start, end);

        Mockito.doReturn(null)
                .when(redisIntakeService)
                .getScore(memberId, foodId1, period);
        Mockito.doReturn(null)
                .when(redisIntakeService)
                .getScore(memberId, foodId2, period);
        Mockito.doReturn(null)
                .when(redisIntakeService)
                .getScore(memberId, foodId3, period);

        Set<ZSetOperations.TypedTuple<String>> setTypedTuple = new HashSet<>();

        ZSetOperations.TypedTuple<String> e1 = ZSetOperations.TypedTuple.of(String.valueOf(foodId1), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e2 = ZSetOperations.TypedTuple.of(String.valueOf(foodId2), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e3 = ZSetOperations.TypedTuple.of(String.valueOf(foodId3), Double.valueOf(1));
        setTypedTuple.add(e1);
        setTypedTuple.add(e2);
        setTypedTuple.add(e3);

        Mockito.doReturn(setTypedTuple)
                .when(redisIntakeService)
                .getRankRangeSet(memberId, period);
        Mockito.doReturn(
                        Optional.of(RedisFood.builder()
                                .name("food1")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId1);
        Mockito.doReturn(
                        Optional.of(RedisFood.builder()
                                .name("food2")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId2);
        Mockito.doReturn(
                        Optional.of(RedisFood.builder()
                                .name("food3")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId3);

        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(memberId, period);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).value()).isEqualTo(1);
        assertThat(result.get(1).value()).isEqualTo(1);
        assertThat(result.get(2).value()).isEqualTo(1);

    }

    @DisplayName("섭취목록조회시_데이터가있으면_레디스에저장_레디스에저장되어있는멤버")
    @Test
    void givenExistingIntakeRankWhenGetIntakeRankListThenIntakeRankList() {

        // given
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();

        Long foodId1 = 1L;
        Long foodId2 = 12L;
        Long foodId3 = 13L;

        Mockito.doReturn(
                        List.of(
                                IntakeEntity.builder().foodId(foodId1).build(),
                                IntakeEntity.builder().foodId(foodId2).build(),
                                IntakeEntity.builder().foodId(foodId3).build()
                        )
                )
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(memberId, start, end);

        Mockito.doReturn(Double.valueOf(2))
                .when(redisIntakeService)
                .getScore(memberId, foodId3, period);
        Mockito.doReturn(null)
                .when(redisIntakeService)
                .getScore(memberId, foodId2, period);
        Mockito.doReturn(null)
                .when(redisIntakeService)
                .getScore(memberId, foodId1, period);

        Set<ZSetOperations.TypedTuple<String>> setTypedTuple = new HashSet<>();

        ZSetOperations.TypedTuple<String> e1 = ZSetOperations.TypedTuple.of(String.valueOf(foodId1), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e2 = ZSetOperations.TypedTuple.of(String.valueOf(foodId2), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e3 = ZSetOperations.TypedTuple.of(String.valueOf(foodId3), Double.valueOf(3));
        setTypedTuple.add(e1);
        setTypedTuple.add(e2);
        setTypedTuple.add(e3);

        Mockito.doReturn(setTypedTuple)
                .when(redisIntakeService)
                .getRankRangeSet(memberId, period);
        Mockito.doReturn(
                        Optional.of(RedisFood.builder()
                                .name("food1")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId1);
        Mockito.doReturn(
                        Optional.of(RedisFood.builder()
                                .name("food2")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId2);
        Mockito.doReturn(
                        Optional.of(RedisFood.builder()
                                .name("food3")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId3);

        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(memberId, period);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).value()).isEqualTo(1);
        assertThat(result.get(1).value()).isEqualTo(1);
        assertThat(result.get(2).value()).isEqualTo(3);

    }


}
