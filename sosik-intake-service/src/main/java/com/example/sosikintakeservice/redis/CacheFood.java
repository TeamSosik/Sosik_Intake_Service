package com.example.sosikintakeservice.redis;

import com.example.sosikintakeservice.dto.api.ResponseGetFood;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@RedisHash(
        value = "cacheFood",
        timeToLive = 60 * 30 // 30분
)
public class CacheFood {

    @Id
    private Long foodId;
    @Indexed
    private String name;
    private BigDecimal carbo;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal kcal;
    private BigDecimal sugars;
    private String manufacturer;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public CacheFood(
            Long foodId,
            String name,
            BigDecimal carbo,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal kcal,
            BigDecimal sugars,
            String manufacturer,
            String image,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
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

    public static CacheFood create(ResponseGetFood responseGetFood) {

        return CacheFood.builder()
                .foodId(responseGetFood.getFoodId())
                .name(responseGetFood.getName())
                .carbo(responseGetFood.getCarbo())
                .protein(responseGetFood.getProtein())
                .fat(responseGetFood.getFat())
                .sugars(responseGetFood.getSugars())
                .kcal(responseGetFood.getKcal())
                .manufacturer(responseGetFood.getManufacturer())
                .image(responseGetFood.getImage())
                .createdAt(responseGetFood.getCreatedAt())
                .modifiedAt(responseGetFood.getModifiedAt())
                .build();
    }


}
