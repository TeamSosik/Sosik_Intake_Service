package com.example.sosikintakeservice.dto.request;

import com.example.sosikintakeservice.model.entity.Category;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RequestIntake(Long foodId,
                            BigDecimal calculationCarbo,
                            BigDecimal calculationProtein,
                            BigDecimal calculationFat,
                            BigDecimal calculationKcal,
                            @Min(value = 1, message = "1보다 작을 수 없습니다.") @Max(value = 1000, message = "1000보다 클 수 없습니다.")
                            Integer foodAmount,
                            Category category
){
    
}
