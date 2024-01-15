package com.example.sosikintakeservice.dto.response;

import lombok.Builder;

@Builder
public record ResponseGetIntakeRank(Long intakeId,
                                    String name,
                                    double value,
                                    int rank
                                    ) {


}
