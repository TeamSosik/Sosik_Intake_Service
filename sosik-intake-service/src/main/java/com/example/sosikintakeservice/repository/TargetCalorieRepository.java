package com.example.sosikintakeservice.repository;

import com.example.sosikintakeservice.model.entity.DayTargetCalorieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TargetCalorieRepository extends JpaRepository<DayTargetCalorieEntity, Long> {
    Optional<DayTargetCalorieEntity>findTopByOrderByLastCreatedDateDesc();
}
