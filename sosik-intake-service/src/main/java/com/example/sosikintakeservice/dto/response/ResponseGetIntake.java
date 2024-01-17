package com.example.sosikintakeservice.dto.response;

import com.example.sosikintakeservice.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
public record ResponseGetIntake(
        Long id,
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

}
