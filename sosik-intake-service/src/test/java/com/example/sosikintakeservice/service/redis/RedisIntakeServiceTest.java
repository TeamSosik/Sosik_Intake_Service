package com.example.sosikintakeservice.service.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DOUBLE;

@SpringBootTest
public class RedisIntakeServiceTest {

    @Autowired
    RedisIntakeService redisIntakeService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    // delete 하기
    void init(Long memberId, int period) {

        String key = "IntakeRank" + ":" + memberId + ":" + period;
        redisTemplate.delete(key);
    }

    @DisplayName("redisIntakeService 잘 띄워지는지 확인")
    @Test
    void givenWhenThenSuccessRedisIntakeServicePresent() {

        assertThat(redisIntakeService).isNotNull();
        assertThat(redisTemplate).isNotNull();
    }


    @DisplayName("섭취랭크불러오기_존재하지_않는다.")
    @Test
    void givenIntakeRankInfoWhenGetIntakeRankThenNothing() {

        // given
        Long memberId = 1L;
        Integer period = 30;
        Long intakeId = 2L;
        String keyPrefix = "IntakeRank";
        int score = 2;

        String key = keyPrefix + ":" + memberId + ":" + period;
        String stringIntakeId = String.valueOf(intakeId);
        redisTemplate.opsForZSet().add(key, stringIntakeId, score);


        // when
        Long notIntakeId = 3L;
//        Double findScore = redisTemplate.opsForZSet().score(key, findStringIntakeId);
        Double findScore = redisIntakeService.getScore(memberId, notIntakeId, period);

        // then
        assertThat(findScore).isNull();

        // reset
        init(memberId, period);
    }

    @DisplayName("섭취랭크불러오기_존재한다.")
    @Test
    void givenIntakeRankInfoWhenGetIntakeRankThenOne() {

        // given
        Long memberId = 1L;
        Long intakeId = 2L;
        Integer period = 30;
        String keyPrefix = "IntakeRank";
        int score = 1;

        String key = keyPrefix + ":" + memberId + ":" + period;
        String stringIntakeId = String.valueOf(intakeId);
        redisTemplate.opsForZSet().add(key, stringIntakeId, score);

        // when
        Long findIntakeId = 2L;
        Double findScore = redisIntakeService.getScore(memberId, findIntakeId, period);

        // then
        assertThat(findScore).isNotNull();
        assertThat(findScore).isEqualTo(1);

        // reset
        init(memberId, period);
    }

    @DisplayName("섭취랭크등록")
    @Test
    void givenIntakeRankWhenSaveThenSuccess() {

        // given
        Long memberId = 1L;
        int period = 20;
        Long intakeId = 1L;
        Integer value = 2;

        // when
        redisIntakeService.save(memberId, intakeId, period, value);

        String key = "IntakeRank" + ":" + memberId + ":" + period;

        String findIntakeId = String.valueOf(intakeId);
        Double findScore = redisTemplate.opsForZSet().score(key, findIntakeId);
        // then
        assertThat(findScore).isNotNull();
        assertThat(findScore).isEqualTo(Double.valueOf(value));

        // reset
        init(memberId, period);
    }

    @DisplayName("섭취랭킹불러오기")
    @Test
    void givenIntakesWhenGetRankSetThenSucess() {

        // given
        Long memberId = 1L;
        Integer period = 30;
        String keyPrefix = "IntakeRank";
        String key = keyPrefix + ":" + memberId + ":" + period;

        Long intakeId = 1L;
        int score = 1;

        Long intakeId2 = 2L;
        int score2 = 11;

        Long intakeId3 = 3L;
        int score3 = 10;

        redisTemplate.opsForZSet().add(key, String.valueOf(intakeId), score);
        redisTemplate.opsForZSet().add(key, String.valueOf(intakeId2), score2);
        redisTemplate.opsForZSet().add(key, String.valueOf(intakeId3), score3);

        // when
        Set<ZSetOperations.TypedTuple<String>> getRankSet = redisIntakeService.getRankRangeSet(memberId, period);

        List<Map<String, Double>> result = getRankSet.stream()
                .map(data -> Map.of(data.getValue(), data.getScore()))
                .collect(Collectors.toList());

        // then
        assertThat(result.size()).isEqualTo(3);

        Map<String, Double> rank1 = result.stream().findFirst().get();
        assertThat(rank1.get(String.valueOf(intakeId2))).isEqualTo(Double.valueOf(score2));

        // reset
        init(memberId, period);
    }






}
