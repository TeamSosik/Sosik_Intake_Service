package com.example.sosikintakeservice.model.entity;

import com.example.sosikintakeservice.dto.request.RequestTargetCalorie;
import com.example.sosikintakeservice.dto.request.RequestUpdateTargetCalorie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor
@Table
public class DayTargetCalorieEntity extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Integer dayTargetKcal;


    @Builder
    public DayTargetCalorieEntity(
            final Long memberId,
            final Integer dayTargetKcal
    ){  this.memberId = memberId;
        this.dayTargetKcal = dayTargetKcal;
    }

    public void updateTargetCalorie(RequestUpdateTargetCalorie requestUpdateTargetCalorie) {
        this.dayTargetKcal = requestUpdateTargetCalorie.dayTargetKcal();
    }

    public static DayTargetCalorieEntity buildDayTargetCalorie(Long memberId, RequestTargetCalorie requestTargetCalorie){
        return DayTargetCalorieEntity.builder()
                .memberId(memberId)
                .dayTargetKcal(requestTargetCalorie.dayTargetKcal())
                .build();
    }
}
