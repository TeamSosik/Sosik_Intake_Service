package com.example.sosikintakeservice.repository;

import com.example.sosikintakeservice.model.entity.Category;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class IntakeRepositoryTest {

    @Autowired
    IntakeRepository intakeRepository;

    @DisplayName("respository 잘 띄워지는지 테스트")
    @Test
    void givenWhenThenSuccessRespotioryPressent() {


        assertThat(intakeRepository).isNotNull();
    }

    @DisplayName("intake조회 사이즈가0")
    @Test
    void givenWhenFindIntakeByMemberIdAndCreatedAtBetweenThenSizeIs0() {

        // given

        // when
        Long memberId = 1L;
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        System.out.println("start : " + start);
        System.out.println("end : " + end);

        List<IntakeEntity> intakeList = intakeRepository.findByMemberIdAndCreatedAtBetween(memberId, start, end);

        // then
        assertThat(intakeList.size()).isEqualTo(0);
    }

    @DisplayName("intake조회 사이즈가2")
    @Test
    void givenWhenFindIntakeByMemberIdAndCreatedAtBetweenThenSizeIs2() {

        // given
        IntakeEntity intake1 = IntakeEntity.builder()
                .memberId(1L)
                .foodId(1L)
                .category(Category.BREAKFAST)
                .build();
        IntakeEntity intake2 = IntakeEntity.builder()
                .memberId(1L)
                .foodId(1L)
                .category(Category.BREAKFAST)
                .build();
        IntakeEntity intake3 = IntakeEntity.builder()
                .memberId(1L)
                .foodId(1L)
                .category(Category.BREAKFAST)
                .build();

        intakeRepository.save(intake1);
        intakeRepository.save(intake2);
        intakeRepository.save(intake3);

        // update intake3 createdAt
        intake3.setCreatedAt(LocalDate.now().minusDays(1));

        // when
        Long memberId = 1L;
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        System.out.println("start : " + start);
        System.out.println("end : " + end);

        List<IntakeEntity> intakeList = intakeRepository.findByMemberIdAndCreatedAtBetween(memberId, start, end);

        // then
        assertThat(intakeList.size()).isEqualTo(2);
    }













}
