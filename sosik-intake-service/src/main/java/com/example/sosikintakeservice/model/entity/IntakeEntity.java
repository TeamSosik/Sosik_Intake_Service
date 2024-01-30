package com.example.sosikintakeservice.model.entity;

import com.example.sosikintakeservice.dto.request.RequestIntake;
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
        this.calculationCarbo = calculationCarbo;
        this.calculationProtein = calculationProtein;
        this.calculationFat = calculationFat;
        this.calculationKcal = calculationKcal;
        this.foodAmount = foodAmount;
        this.category = category;
    }


    public static IntakeEntity create(Long memberId, RequestIntake intakeDTO) {

        return IntakeEntity.builder()
                .memberId(memberId)
                .foodId(intakeDTO.foodId())
                .calculationCarbo(intakeDTO.calculationCarbo())
                .calculationProtein(intakeDTO.calculationProtein())
                .calculationFat(intakeDTO.calculationFat())
                .calculationKcal(intakeDTO.calculationKcal())
                .foodAmount(intakeDTO.foodAmount())
                .category(intakeDTO.category())
                .build();
    }
}
