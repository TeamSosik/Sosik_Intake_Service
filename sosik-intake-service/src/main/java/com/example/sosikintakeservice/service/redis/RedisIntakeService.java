package com.example.sosikintakeservice.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisIntakeService {

    private final RedisTemplate<String, String> redisTemplate;
    private String KEY_PREFIX = "IntakeRank";
    private long expiredTime = 60 * 10;// 10분


    public Double getScore(Long memberId, Long foodId, Integer period) {

        // key 불러오기
        String key = getKey(memberId, period);

        String stringFoodId = String.valueOf(foodId);

        // redis에서 score 조회하기
        Double value = redisTemplate.opsForZSet().score(key, stringFoodId);

        return value;
    }

    String getKey(Long memberId, Integer period) {

        return KEY_PREFIX + ":" + memberId + ":" + period;
    }

    public void save(Long memberId, Long foodId, int period, Integer value) {

        // key 생성하기
        String key = getKey(memberId, period);

        // sorted set에 저장하기
        String stringFoodId = String.valueOf(foodId);

        redisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);// 만료시간 정하기
        redisTemplate.opsForZSet().add(key, stringFoodId, value);
    }

    public Set<ZSetOperations.TypedTuple<String>> getRankRangeSet(Long memberId, Integer period) {

        // key 생성하기
        String key = getKey(memberId, period);

        // 역순으로 rank 불러오기
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }
}
