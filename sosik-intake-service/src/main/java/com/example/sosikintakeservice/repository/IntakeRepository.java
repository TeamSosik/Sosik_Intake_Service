package com.example.sosikintakeservice.repository;

import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IntakeRepository extends JpaRepository<IntakeEntity, Long> {
    List<IntakeEntity> findByMemberIdAndCreatedAt(Long memberId, LocalDate localDateTime);

    List<IntakeEntity> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDate start2, LocalDate end);
}
