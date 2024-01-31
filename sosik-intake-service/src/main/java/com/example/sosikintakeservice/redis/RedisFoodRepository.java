package com.example.sosikintakeservice.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisFoodRepository extends CrudRepository<CacheFood, Long> {
    List<CacheFood> findAll();
}
