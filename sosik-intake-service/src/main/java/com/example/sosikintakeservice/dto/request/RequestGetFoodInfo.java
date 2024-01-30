package com.example.sosikintakeservice.dto.request;

import lombok.Builder;

@Builder
public record RequestGetFoodInfo(String kcal,
                                String carbo,
                                String protein,
                                String fat,
                                String total
) {

}
