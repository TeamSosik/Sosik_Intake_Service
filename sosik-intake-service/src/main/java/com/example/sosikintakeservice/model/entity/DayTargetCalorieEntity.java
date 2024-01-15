package com.example.sosikintakeservice.model.entity;

import com.example.sosikintakeservice.dto.request.UpdateTargetCalorie;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Integer dailyIntakePurpose;

    @Builder
    public DayTargetCalorieEntity(
            final Long memberId,
            final Integer dayTargetKcal,
            final Integer dailyIntakePurpose
    ){  this.memberId = memberId;
        this.dayTargetKcal = dayTargetKcal;
        this.dailyIntakePurpose = dailyIntakePurpose;
    }

    public void updateTargetCalorie(UpdateTargetCalorie updateTargetCalorie) {
        this.dayTargetKcal = updateTargetCalorie.dayTargetKcal();
        this.dailyIntakePurpose = updateTargetCalorie.dailyIntakePurpose();
    }
}
