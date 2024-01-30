package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.api.FoodServiceApi;
import com.example.sosikintakeservice.dto.IntakeRankCondition;
import com.example.sosikintakeservice.dto.api.ResponseGetFood;
import com.example.sosikintakeservice.dto.request.RequestGetFoodInfo;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.Category;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import com.example.sosikintakeservice.redis.CacheFood;
import com.example.sosikintakeservice.redis.RedisFoodRepository;
import com.example.sosikintakeservice.repository.IntakeRepository;
import com.example.sosikintakeservice.service.redis.RedisIntakeService;
import org.assertj.core.api.Assertions;
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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private FoodServiceApi foodServiceApi;

    @DisplayName("섭취 음식 생성시 정상적으로 작동된다.")
    @Test
    void givenTestIntakeWhenCreateIntakeThenSuccess() {
        RequestIntake testIntakeDTO = testIntakeDTO();
        Long memberId = 1L;
        given(intakeRepository.save(any())).willReturn(any());
        assertThat(intakeService.createIntake(memberId, testIntakeDTO)).isEqualTo("ok");
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
        Long memberId = 1L;

        RequestIntake testIntakeDTO1 = RequestIntake.builder()
                .foodId(2L)
                .calculationCarbo(calculationCarbo)
                .calculationFat(calculationFat)
                .calculationKcal(calculationKcal)
                .calculationProtein(calculationProtein)
                .category(category)
                .foodAmount(150)
                .build();
        System.out.println(testIntakeDTO1);

        given(intakeRepository.save(any())).willReturn(any());
        assertThat(intakeService.createIntake(memberId, testIntakeDTO1)).isEqualTo("ok");
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
                .foodId(2L)
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
                .id(1L)
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
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();

        Mockito.doReturn(new ArrayList<>())
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(memberId, start, end);

        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        assertThat(result.size()).isEqualTo(0);

    }

    @DisplayName("레디스에key가삭제되었는지_확인")
    @Test
    void givenIntakeEntityWhenDeleteRedisKeyThenCheckDeleteMethod() {

        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        IntakeEntity intake = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .build();
        ArrayList<IntakeEntity> intakeList = new ArrayList<>();
        intakeList.add(intake);

        Mockito.doReturn(intakeList)
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(Mockito.any(Long.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
        // TODO : 이 stub가 꼭 필요할까?????
//        Mockito.doNothing()
//                .when(redisIntakeService)
//                .delete(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Integer.class));

        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        Mockito.verify(redisIntakeService, Mockito.times(1)).delete(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Integer.class));
    }


    @DisplayName("레디스데이터저장_food_레디스에없을때_확인")
    @Test
    void givenFoodTypeWhenSaveThenSuccessButNotExist() {

        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        IntakeEntity intake = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .build();
        ArrayList<IntakeEntity> intakeList = new ArrayList<>();
        intakeList.add(intake);

        Mockito.doReturn(intakeList)
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(Mockito.any(Long.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
        Mockito.doReturn(null)
                .when(redisIntakeService)
                .getScore(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(Integer.class));
        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        Mockito.verify(redisIntakeService, Mockito.times(1)).save(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(Integer.class), Mockito.any(Double.class));
    }

    @DisplayName("레디스데이터저장_food_확인")
    @Test
    void givenFoodTypeWhenSaveThenSuccessButExist() {

        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        IntakeEntity intake = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .build();
        ArrayList<IntakeEntity> intakeList = new ArrayList<>();
        intakeList.add(intake);

        Mockito.doReturn(intakeList)
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(Mockito.any(Long.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
        Mockito.doReturn(1D)
                .when(redisIntakeService)
                .getScore(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(Integer.class));
        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        Mockito.verify(redisIntakeService, Mockito.times(1)).save(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(Integer.class), Mockito.any(Double.class));
    }

    @DisplayName("레디스데이터저장_확인")
    @ParameterizedTest
    @MethodSource("getSaveByCondition")
    void givenConditionForRankTypeWhenGetListThenSuccessSave(
            String rankType
    ) {

        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType(rankType)
                .build();
        Long memberId = 1L;
        int period = 30;
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        IntakeEntity intake = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .calculationKcal(BigDecimal.valueOf(12.25))
                .build();
        IntakeEntity intake2 = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .calculationKcal(BigDecimal.valueOf(13.25))
                .build();
        ArrayList<IntakeEntity> intakeList = new ArrayList<>();
        intakeList.add(intake);
        intakeList.add(intake2);

        Mockito.doReturn(intakeList)
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(Mockito.any(Long.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));
        Mockito.doReturn(1D)
                .when(redisIntakeService)
                .getScore(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(Integer.class));
        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        Mockito.verify(redisIntakeService, Mockito.times(2)).save(Mockito.any(String.class), Mockito.any(Long.class), Mockito.any(Long.class), Mockito.any(Integer.class), Mockito.any(Double.class));
    }

    private static Stream<Arguments> getSaveByCondition() {

        return Stream.of(
                Arguments.of("food"),
                Arguments.of("kcal")
        );
    }

    @DisplayName("랭크값을불러오기_확인")
    @Test
    void givenRankConditionAndItakeEntitiesWhenGetRankListThenSuccess() {

        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("kcal")
                .build();
        Long memberId = 1L;
        int period = 30;
        IntakeEntity intake = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .calculationKcal(BigDecimal.valueOf(12.25))
                .build();
        IntakeEntity intake2 = IntakeEntity.builder()
                .id(1L)
                .foodId(memberId)
                .calculationKcal(BigDecimal.valueOf(13.25))
                .build();
        ArrayList<IntakeEntity> intakeList = new ArrayList<>();
        intakeList.add(intake);
        intakeList.add(intake2);

        Mockito.doReturn(intakeList)
                .when(intakeRepository)
                .findByMemberIdAndCreatedAtBetween(Mockito.any(Long.class), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class));

        // when
        intakeService.getRankList(intakeRankCondition, memberId, period);
        // then
        Mockito.verify(redisIntakeService, Mockito.times(1))
                .getRankRangeSet(intakeRankCondition.rankType(), memberId, period);
    }

    @DisplayName("레디스에데이터가있을때_DTO생성_확인")
    @Test
    void givenRankConditionAndIntakeEntitiesAndIsCacheTrueWhenGetRankListThenCheckSuccess() {
        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
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
                .getScore(intakeRankCondition.rankType(), memberId, foodId3, period);
        Mockito.doReturn(Double.valueOf(1))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId2, period);
        Mockito.doReturn(Double.valueOf(3))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId1, period);

        Set<ZSetOperations.TypedTuple<String>> setTypedTuple = new HashSet<>();

        ZSetOperations.TypedTuple<String> e1 = ZSetOperations.TypedTuple.of(String.valueOf(foodId1), Double.valueOf(2));
        ZSetOperations.TypedTuple<String> e2 = ZSetOperations.TypedTuple.of(String.valueOf(foodId2), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e3 = ZSetOperations.TypedTuple.of(String.valueOf(foodId3), Double.valueOf(3));
        setTypedTuple.add(e1);
        setTypedTuple.add(e2);
        setTypedTuple.add(e3);

        Mockito.doReturn(setTypedTuple)
                .when(redisIntakeService)
                .getRankRangeSet(intakeRankCondition.rankType(), memberId, period);
        Mockito.doReturn(
                        Optional.of(CacheFood.builder()
                                .name("food1")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId1);
        Mockito.doReturn(
                        Optional.of(CacheFood.builder()
                                .name("food2")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId2);
        Mockito.doReturn(
                        Optional.of(CacheFood.builder()
                                .name("food3")
                                .build())
                )
                .when(redisFoodRepository)
                .findById(foodId3);

        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).value()).isEqualTo(2);
        assertThat(result.get(1).value()).isEqualTo(1);
        assertThat(result.get(2).value()).isEqualTo(3);
    }

    @DisplayName("레디스에데이터가없을때_음식서비스를조회시_데이터가없으면_예외를던진다.")
    @Test
    void givenRankConditionAndIntakeEntitiesAndIsCacheFalseWhenGetRankListThenThrowException() {
        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
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
                .getScore(intakeRankCondition.rankType(), memberId, foodId3, period);
        Mockito.doReturn(Double.valueOf(1))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId2, period);
        Mockito.doReturn(Double.valueOf(3))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId1, period);

        Set<ZSetOperations.TypedTuple<String>> setTypedTuple = new LinkedHashSet<>();

        ZSetOperations.TypedTuple<String> e1 = ZSetOperations.TypedTuple.of(String.valueOf(foodId1), Double.valueOf(2));
        ZSetOperations.TypedTuple<String> e2 = ZSetOperations.TypedTuple.of(String.valueOf(foodId2), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e3 = ZSetOperations.TypedTuple.of(String.valueOf(foodId3), Double.valueOf(3));
        setTypedTuple.add(e3);
        setTypedTuple.add(e1);
        setTypedTuple.add(e2);

        Mockito.doReturn(setTypedTuple)
                .when(redisIntakeService)
                .getRankRangeSet(intakeRankCondition.rankType(), memberId, period);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId1);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId2);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId3);

        ResponseGetFood getFood1 = ResponseGetFood.builder()
                .foodId(foodId3)
                .name("감자1")
                .build();
        ResponseGetFood getFood2 = null;
        ResponseGetFood getFood3 = ResponseGetFood.builder()
                .foodId(foodId1)
                .name("감자3")
                .build();

        Mockito.doReturn(
                        Result.success(getFood1)
                )
                .when(foodServiceApi)
                .getFood(foodId3);
        Mockito.doReturn(
                        Result.success(getFood3)
                )
                .when(foodServiceApi)
                .getFood(foodId1);
        Mockito.doReturn(
                    Result.success(null)
                )
                .when(foodServiceApi)
                .getFood(foodId2);

        // when
        ApplicationException result = assertThrows(ApplicationException.class, () -> {
            intakeService.getRankList(intakeRankCondition, memberId, period);
        });

        Assertions.assertThat(result.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("레디스에데이터가없을때_음식서비스를조회시_음식서비스와커넥션이끊어지면_예외를던진다.")
    @Test
    void givenRankConditionAndIntakeEntitiesAndIsCacheFalseWhenFailingConnectionThenThrowException() {
        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
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
                .getScore(intakeRankCondition.rankType(), memberId, foodId3, period);
        Mockito.doReturn(Double.valueOf(1))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId2, period);
        Mockito.doReturn(Double.valueOf(3))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId1, period);

        Set<ZSetOperations.TypedTuple<String>> setTypedTuple = new LinkedHashSet<>();

        ZSetOperations.TypedTuple<String> e1 = ZSetOperations.TypedTuple.of(String.valueOf(foodId1), Double.valueOf(2));
        ZSetOperations.TypedTuple<String> e2 = ZSetOperations.TypedTuple.of(String.valueOf(foodId2), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e3 = ZSetOperations.TypedTuple.of(String.valueOf(foodId3), Double.valueOf(3));
        setTypedTuple.add(e3);
        setTypedTuple.add(e1);
        setTypedTuple.add(e2);

        Mockito.doReturn(setTypedTuple)
                .when(redisIntakeService)
                .getRankRangeSet(intakeRankCondition.rankType(), memberId, period);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId1);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId2);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId3);

        ResponseGetFood getFood1 = ResponseGetFood.builder()
                .foodId(foodId3)
                .name("감자1")
                .build();
        ResponseGetFood getFood2 = null;
        ResponseGetFood getFood3 = ResponseGetFood.builder()
                .foodId(foodId1)
                .name("감자3")
                .build();

        Mockito.doReturn(
                        Result.success(getFood1)
                )
                .when(foodServiceApi)
                .getFood(foodId3);
        Mockito.doReturn(
                        Result.success(getFood3)
                )
                .when(foodServiceApi)
                .getFood(foodId1);
        Mockito.doThrow(
                    new RestClientException("서버 연결 에러")
                )
                .when(foodServiceApi)
                .getFood(foodId2);


        // when
        ApplicationException result = assertThrows(ApplicationException.class, () -> {
            intakeService.getRankList(intakeRankCondition, memberId, period);
        });

        Assertions.assertThat(result.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("레디스에데이터가없을때_음식서비스를조회한다_DTO생성_확인")
    @Test
    void givenRankConditionAndIntakeEntitiesAndIsCacheFalseWhenGetRankListThenCheckSuccess() {
        // given
        // given
        IntakeRankCondition intakeRankCondition = IntakeRankCondition.builder()
                .rankType("food")
                .build();
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
                .getScore(intakeRankCondition.rankType(), memberId, foodId3, period);
        Mockito.doReturn(Double.valueOf(1))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId2, period);
        Mockito.doReturn(Double.valueOf(3))
                .when(redisIntakeService)
                .getScore(intakeRankCondition.rankType(), memberId, foodId1, period);

        Set<ZSetOperations.TypedTuple<String>> setTypedTuple = new LinkedHashSet<>();

        ZSetOperations.TypedTuple<String> e1 = ZSetOperations.TypedTuple.of(String.valueOf(foodId1), Double.valueOf(2));
        ZSetOperations.TypedTuple<String> e2 = ZSetOperations.TypedTuple.of(String.valueOf(foodId2), Double.valueOf(1));
        ZSetOperations.TypedTuple<String> e3 = ZSetOperations.TypedTuple.of(String.valueOf(foodId3), Double.valueOf(3));
        setTypedTuple.add(e3);
        setTypedTuple.add(e1);
        setTypedTuple.add(e2);

        Mockito.doReturn(setTypedTuple)
                .when(redisIntakeService)
                .getRankRangeSet(intakeRankCondition.rankType(), memberId, period);
        Mockito.doReturn(
                      Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId1);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId2);
        Mockito.doReturn(
                        Optional.empty()
                )
                .when(redisFoodRepository)
                .findById(foodId3);

        ResponseGetFood getFood1 = ResponseGetFood.builder()
                .foodId(foodId3)
                .name("감자1")
                .build();
        ResponseGetFood getFood2 = ResponseGetFood.builder()
                .foodId(foodId2)
                .name("감자2")
                .build();
        ResponseGetFood getFood3 = ResponseGetFood.builder()
                .foodId(foodId1)
                .name("감자3")
                .build();

        Mockito.doReturn(
                        Result.success(getFood1)
                )
                .when(foodServiceApi)
                .getFood(foodId3);
        Mockito.doReturn(
                        Result.success(getFood3)
        )
                .when(foodServiceApi)
                .getFood(foodId1);
        Mockito.doReturn(
                        Result.success(getFood2)
                )
                .when(foodServiceApi)
                .getFood(foodId2);


        // when
        List<ResponseGetIntakeRank> result = intakeService.getRankList(intakeRankCondition, memberId, period);

        // then
        Mockito.verify(redisFoodRepository, Mockito.times(3)).save(Mockito.any(CacheFood.class));
        Mockito.verify(foodServiceApi, Mockito.times(3)).getFood(Mockito.any(Long.class));
        Assertions.assertThat(result.get(0).foodId()).isEqualTo(foodId3);
        Assertions.assertThat(result.get(0).name()).isEqualTo("감자1");
    }

}
