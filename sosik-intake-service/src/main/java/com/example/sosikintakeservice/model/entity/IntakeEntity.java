package com.example.sosikintakeservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "intake")
public class IntakeEntity extends AuditingFields{
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long foodId;

    @Column(nullable = false)
    private Long dayTargetCalorieId;

    private BigDecimal calculationCarbo;
    private BigDecimal calculationProtein;
    private BigDecimal calculationFat;
    private BigDecimal calculationKcal;
    private Integer foodAmount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Category category;

    @Builder
    public IntakeEntity (
            final Long id,
            final Long memberId,
            final Long foodId,
            final Long dayTargetCalorieId,
            final BigDecimal calculationCarbo,
            final BigDecimal calculationProtein,
            final BigDecimal calculationFat,
            final BigDecimal calculationKcal,
            final Integer foodAmount,
            final Category category

    ){
        this.id = id;
        this.memberId = memberId;
        this.foodId = foodId;
        this.dayTargetCalorieId = dayTargetCalorieId;
        this.calculationCarbo = calculationCarbo;
        this.calculationProtein = calculationProtein;
        this.calculationFat = calculationFat;
        this.calculationKcal = calculationKcal;
        this.foodAmount = foodAmount;
        this.category = category;
    }


}
