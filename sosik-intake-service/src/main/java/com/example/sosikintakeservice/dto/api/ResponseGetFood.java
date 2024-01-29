package com.example.sosikintakeservice.dto.api;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseGetFood {

    private Long foodId;
    private String name;
    private BigDecimal carbo;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal sugars;
    private BigDecimal kcal;
    private String manufacturer;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ResponseGetFood(Long foodId,
                           String name,
                           BigDecimal carbo,
                           BigDecimal protein,
                           BigDecimal fat,
                           BigDecimal sugars,
                           BigDecimal kcal,
                           String manufacturer,
                           String image,
                           LocalDateTime createdAt,
                           LocalDateTime modifiedAt) {
        this.foodId = foodId;
        this.name = name;
        this.carbo = carbo;
        this.protein = protein;
        this.fat = fat;
        this.sugars = sugars;
        this.kcal = kcal;
        this.manufacturer = manufacturer;
        this.image = image;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

}
