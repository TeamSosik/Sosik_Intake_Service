package com.example.sosikintakeservice.dto.request;

import com.example.sosikintakeservice.model.entity.AuditingFields;
import com.example.sosikintakeservice.model.entity.Category;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record RequestIntake(
    Long memberId,
    Long foodId,
    Long dayTargetCalorieId,
    BigDecimal calculationCarbo,
    BigDecimal calculationProtein,
    BigDecimal calculationFat,
    BigDecimal calculationKcal,
    Integer foodAmount,
    Category category
){
    
}
