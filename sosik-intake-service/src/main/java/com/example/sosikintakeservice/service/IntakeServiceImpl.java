package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import com.example.sosikintakeservice.redis.RedisFood;
import com.example.sosikintakeservice.redis.RedisFoodRepository;
import com.example.sosikintakeservice.repository.IntakeRepository;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IntakeServiceImpl implements IntakeService{
    @Autowired
    private IntakeRepository intakeRepository;
    @Autowired
    private RedisFoodRepository redisFoodRepository;

    public String createIntake(RequestIntake intakeDTO) {
        IntakeEntity intake = IntakeEntity.builder()
                .memberId(intakeDTO.memberId())
                .foodId(intakeDTO.foodId())
                .dayTargetCalorieId(intakeDTO.dayTargetCalorieId())
                .calculationCarbo(intakeDTO.calculationCarbo())
                .calculationProtein(intakeDTO.calculationProtein())
                .calculationFat(intakeDTO.calculationFat())
                .calculationKcal(intakeDTO.calculationKcal())
                .foodAmount(intakeDTO.foodAmount())
                .category(intakeDTO.category())
                .build();
        intakeRepository.save(intake);

        return "ok";
    }

    public List<ResponseGetIntake> getIntakes(Long memberId, LocalDate createdAt) {
        List<IntakeEntity> intakeEntities = intakeRepository.findByMemberIdAndCreatedAt(memberId,createdAt);
        System.out.println(intakeEntities);
        return intakeEntities.stream()
                .map(intakeEntity -> {
                    Optional<RedisFood> optionalRedisFood = redisFoodRepository.findById(intakeEntity.getFoodId());
                    if (optionalRedisFood.isPresent()) {
                        RedisFood redisFood = optionalRedisFood.get();

                        String name = redisFood.getName();
                        System.out.println("==========================");
                        System.out.println(name);
                        System.out.println(optionalRedisFood);

                        return ResponseGetIntake.builder()
                                .memberId(intakeEntity.getMemberId())
                                .foodId(intakeEntity.getFoodId())
                                .name(redisFood.getName())
                                .dayTargetCalorieId(intakeEntity.getDayTargetCalorieId())
                                .calculationCarbo(intakeEntity.getCalculationCarbo())
                                .calculationProtein(intakeEntity.getCalculationProtein())
                                .calculationFat(intakeEntity.getCalculationFat())
                                .calculationKcal(intakeEntity.getCalculationKcal())
                                .foodAmount(intakeEntity.getFoodAmount())
                                .category(intakeEntity.getCategory())
                                .build();
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    public String deleteIntake(Long intakeId) {
        if(intakeRepository.findById(intakeId)==null){
            throw new ApplicationException(ErrorCode.INTAKE_NOT_FOUND);
        }
        intakeRepository.deleteById(intakeId);
        return "ok";
    }

}
