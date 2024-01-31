package com.example.sosikintakeservice.dto.response;

import lombok.Builder;

@Builder
public record ResponseGetIntakeRank(Long foodId,
                                    String name,
                                    double value,
                                    int rank
) {

}
