package com.example.sosikintakeservice.model.entity;

import com.example.sosikintakeservice.dto.request.RequestUpdateTargetCalorie;
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
}
