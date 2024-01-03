package com.example.sosikintakeservice.repository;

import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IntakeRepository extends JpaRepository<IntakeEntity, Long> {
    List<IntakeEntity> findByMemberIdAndCreatedAt(Long memberId,LocalDateTime localDateTime);

}
