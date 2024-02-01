package com.example.sosikintakeservice.dto.response;

import lombok.Builder;

@Builder
public record ResponseGetIntakeRank(Long foodId,
                                    String name,
                                    double value,
                                    int rank
) {

    public static ResponseGetIntakeRank create(Long foodId, String name, Double value) {
        return  ResponseGetIntakeRank.builder()
                .foodId(foodId)
                .name(name)
                .value(value)
                .build();
    }
}
