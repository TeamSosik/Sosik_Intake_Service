package com.example.sosikintakeservice.dto.response.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisFoodRepository extends CrudRepository<RedisFood, Long> {

}
