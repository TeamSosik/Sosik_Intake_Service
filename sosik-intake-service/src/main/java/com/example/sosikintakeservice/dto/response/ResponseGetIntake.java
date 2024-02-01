package com.example.sosikintakeservice.dto.response;

import com.example.sosikintakeservice.model.entity.Category;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ResponseGetIntake(Long id,
                                Long memberId,
                                Long foodId,
                                String name,
                                Long dayTargetCalorieId,
                                BigDecimal calculationCarbo,
                                BigDecimal calculationProtein,
                                BigDecimal calculationFat,
                                BigDecimal calculationKcal,
                                Integer foodAmount,
                                Category category

) {

   public static ResponseGetIntake create(IntakeEntity intakeEntity, String name) {

      return ResponseGetIntake.builder()
              .id(intakeEntity.getId())
              .memberId(intakeEntity.getMemberId())
              .foodId(intakeEntity.getFoodId())
              .name(name)
              .calculationCarbo(intakeEntity.getCalculationCarbo())
              .calculationProtein(intakeEntity.getCalculationProtein())
              .calculationFat(intakeEntity.getCalculationFat())
              .calculationKcal(intakeEntity.getCalculationKcal())
              .foodAmount(intakeEntity.getFoodAmount())
              .category(intakeEntity.getCategory())
              .build();
   }

}
