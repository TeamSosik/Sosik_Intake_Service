package com.example.sosikintakeservice.service.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisIntakeServiceTest {

    @Autowired
    RedisIntakeService redisIntakeService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    // delete 하기
    void init(String keyPrefix, Long memberId, int period) {

        String key = keyPrefix + "IntakeRank" + ":" + memberId + ":" + period;
        redisTemplate.delete(key);
    }

    @DisplayName("redisIntakeService 잘 띄워지는지 확인")
    @Test
    void givenWhenThenSuccessRedisIntakeServicePresent() {

        assertThat(redisIntakeService).isNotNull();
        assertThat(redisTemplate).isNotNull();
    }

    @DisplayName("조회값에따라_다른key가생성된다.")
    @ParameterizedTest
    @MethodSource("GetKeyParams")
    void givenInputtingConditionWhenGetKeyThenSomeIntakeRank(
            String selectCondition,
            String confirmDataPrefix
    ) {
        // given
        Long memberId = 1L;
        int period = 30;

        // when
        String result = redisIntakeService.getKey(selectCondition, memberId, period);

        // then
        String confirmData = confirmDataPrefix + "IntakeRank" + ":" + memberId + ":" + period;
        assertThat(result).isEqualTo(confirmData);
    }

    private static Stream<Arguments> GetKeyParams() {

        return Stream.of(
                Arguments.of("kcal", "Kcal"),
                Arguments.of("food", "Food")
        );
    }


    @DisplayName("섭취랭크불러오기_존재하지_않는다.")
    @Test
    void givenIntakeRankInfoWhenGetIntakeRankThenNothing() {

        // given
        String prefix = "Food";
        String selectCondition = "food";
        Long memberId = 1L;
        Integer period = 30;
        Long foodId = 2L;
        String keyPrefix = "IntakeRank";
        int score = 2;

        String key = "Food" + keyPrefix + ":" + memberId + ":" + period;
        String stringFoodId = String.valueOf(foodId);
        redisTemplate.opsForZSet().add(key, stringFoodId, score);

        // when
        Long notFoodId = 3L;
//        Double findScore = redisTemplate.opsForZSet().score(key, findStringIntakeId);
        Double findScore = redisIntakeService.getScore(selectCondition, memberId, notFoodId, period);

        // then
        assertThat(findScore).isNull();

        // reset
        init(prefix, memberId, period);
    }

    @DisplayName("섭취랭크불러오기_존재한다.")
    @Test
    void givenIntakeRankInfoWhenGetIntakeRankThenOne() {

        // given
        String prefix = "Food";
        String selectCondition = "food";
        Long memberId = 1L;
        Long foodId = 2L;
        Integer period = 30;
        String keyPrefix = "IntakeRank";
        int score = 1;

        String key = prefix + keyPrefix + ":" + memberId + ":" + period;
        String stringFoodId = String.valueOf(foodId);
        redisTemplate.opsForZSet().add(key, stringFoodId, score);

        // when
        Long findFoodId = 2L;
        Double findScore = redisIntakeService.getScore(selectCondition, memberId, findFoodId, period);

        // then
        assertThat(findScore).isNotNull();
        assertThat(findScore).isEqualTo(1);

        // reset

        init(prefix, memberId, period);
    }

    @DisplayName("섭취랭크등록")
    @Test
    void givenIntakeRankWhenSaveThenSuccess() {

        // given
        String prefix = "Food";
        String selectCondition = "food";
        Long memberId = 1L;
        int period = 20;
        Long foodId = 1L;
        Integer value = 2;

        // when
        redisIntakeService.save(selectCondition, memberId, foodId, period, value);

        String key = prefix + "IntakeRank" + ":" + memberId + ":" + period;

        String findFoodId = String.valueOf(foodId);
        Double findScore = redisTemplate.opsForZSet().score(key, findFoodId);
        // then
        assertThat(findScore).isNotNull();
        assertThat(findScore).isEqualTo(Double.valueOf(value));

        // reset
        init(prefix, memberId, period);
    }

    @DisplayName("섭취랭킹불러오기")
    @Test
    void givenIntakesWhenGetRankSetThenSucess() {

        // given
        String prefix = "Food";
        String selectCondition = "food";
        Long memberId = 1L;
        Integer period = 30;
        String keyPrefix = "IntakeRank";
        String key = prefix + keyPrefix + ":" + memberId + ":" + period;

        Long foodId = 1L;
        int score = 1;

        Long foodId2 = 2L;
        int score2 = 11;

        Long foodId3 = 3L;
        int score3 = 10;

        redisTemplate.opsForZSet().add(key, String.valueOf(foodId), score);
        redisTemplate.opsForZSet().add(key, String.valueOf(foodId2), score2);
        redisTemplate.opsForZSet().add(key, String.valueOf(foodId3), score3);

        // when
        Set<ZSetOperations.TypedTuple<String>> getRankSet = redisIntakeService.getRankRangeSet(selectCondition, memberId, period);
        List<Map<String, Double>> result = getRankSet.stream()
                .map(data -> Map.of(data.getValue(), data.getScore()))
                .collect(Collectors.toList());

        // then
        assertThat(result.size()).isEqualTo(3);

        Map<String, Double> rank1 = result.stream().findFirst().get();
        assertThat(rank1.get(String.valueOf(foodId2))).isEqualTo(Double.valueOf(score2));

        // reset
        init(prefix, memberId, period);
    }

    @DisplayName("key삭제 확인")
    @Test
    void givenWhenThen() {

        // given
        String prefix = "Food";
        String selectCondition = "food";
        Long memberId = 1L;
        Long foodId = 2L;
        Integer period = 30;
        String keyPrefix = "IntakeRank";
        int score = 1;
        String key = prefix + keyPrefix + ":" + memberId + ":" + period;

        redisTemplate.opsForZSet().add(key, String.valueOf(foodId), score);

        // when
        redisIntakeService.delete(selectCondition, memberId, period);

        // then
        String findMemberId = String.valueOf(memberId);
        Double result = redisTemplate.opsForZSet().score(key, findMemberId);

        assertThat(result).isNull();
    }






}
