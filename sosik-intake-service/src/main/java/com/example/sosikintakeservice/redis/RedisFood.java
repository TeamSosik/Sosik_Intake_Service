package com.example.sosikintakeservice.redis;

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
        value = "redisFood",
        timeToLive = 60 * 30 // 30분
)
public class RedisFood {

    @Id
    private Long foodId;
    @Indexed
    private String name;
    private BigDecimal carbo;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal kcal;
    private BigDecimal size;
    private String createdBy; // 생성자
    private String modifiedBy;//수정자
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime modifiedAt; //수정일시

    @Builder
    public RedisFood(
            Long foodId,
            String name,
            BigDecimal carbo,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal kcal,
            BigDecimal size,
            String createdBy,
            String modifiedBy,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        this.foodId = foodId;
        this.name = name;
        this.carbo = carbo;
        this.protein = protein;
        this.fat = fat;
        this.kcal = kcal;
        this.size = size;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }


}
