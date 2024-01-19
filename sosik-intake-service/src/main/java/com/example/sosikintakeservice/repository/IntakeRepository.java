package com.example.sosikintakeservice.repository;

import com.example.sosikintakeservice.model.entity.IntakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface IntakeRepository extends JpaRepository<IntakeEntity, Long> {

    List<IntakeEntity> findByMemberIdAndCreatedAt(Long memberId, LocalDate localDate);
    List<IntakeEntity> findByMemberId(Long memberId);
    List<IntakeEntity> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDate start, LocalDate end);
}
