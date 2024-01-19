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


    public Double getScore(Long memberId, Long foodId, Integer period) {

        String key = getKey(memberId, period);

        String stringFoodId = String.valueOf(foodId);

        Double value = redisTemplate.opsForZSet().score(key, stringFoodId);

        return value;
    }

    String getKey(Long memberId, Integer period) {

        return KEY_PREFIX + ":" + memberId + ":" + period;
    }

    public void save(Long memberId, Long foodId, int period, Integer value) {

        String key = getKey(memberId, period);

        String stringFoodId = String.valueOf(foodId);

        redisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);
        redisTemplate.opsForZSet().add(key, stringFoodId, value);
    }

    public Set<ZSetOperations.TypedTuple<String>> getRankRangeSet(Long memberId, Integer period) {

        String key = getKey(memberId, period);

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    public void delete(Long memberId, Integer period) {

        String key = getKey(memberId, period);

        redisTemplate.delete(key);
    }
}
