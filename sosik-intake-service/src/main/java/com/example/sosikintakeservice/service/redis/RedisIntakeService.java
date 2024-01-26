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
    private final String KEY_PREFIX = "IntakeRank";
    private long expiredTime = 60 * 10;


    public Double getScore(String selectCondition, Long memberId, Long foodId, Integer period) {

        String key = getKey(selectCondition, memberId, period);

        String stringFoodId = String.valueOf(foodId);

        Double value = redisTemplate.opsForZSet().score(key, stringFoodId);

        return value;
    }

    // TODO : if, else if를 사용안 하는 방법을 찾아보아야 합니다.
    public String getKey(String selectCondition, Long memberId, Integer period) {

        String prefix = null;
        if("kcal".equalsIgnoreCase(selectCondition)) {
            prefix = "Kcal";
        } else if("food".equalsIgnoreCase(selectCondition)) {
            prefix = "Food";
        }

        return prefix + KEY_PREFIX + ":" + memberId + ":" + period;
    }

    public void save(String selectCondition, Long memberId, Long foodId, int period, Integer value) {

        String key = getKey(selectCondition, memberId, period);

        String stringFoodId = String.valueOf(foodId);

        redisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);
        redisTemplate.opsForZSet().add(key, stringFoodId, value);
    }

    public Set<ZSetOperations.TypedTuple<String>> getRankRangeSet(String selectCondition, Long memberId, Integer period) {

        String key = getKey(selectCondition, memberId, period);

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    public void delete(String selectCondition, Long memberId, Integer period) {

        String key = getKey(selectCondition, memberId, period);

        redisTemplate.delete(key);
    }
}
