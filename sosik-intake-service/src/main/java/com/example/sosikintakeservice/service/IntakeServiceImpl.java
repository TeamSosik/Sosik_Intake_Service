package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.dto.response.redis.RedisFood;
import com.example.sosikintakeservice.dto.response.redis.RedisFoodRepository;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import com.example.sosikintakeservice.repository.IntakeRepository;
import com.example.sosikintakeservice.service.redis.RedisIntakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IntakeServiceImpl implements IntakeService{

    private final IntakeRepository intakeRepository;
    private final RedisIntakeService redisIntakeService;
    private final RedisFoodRepository redisFoodRepository;


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

    public List<ResponseGetIntake> getIntakes(RequestGetIntake requestgetIntake) {
        List<IntakeEntity> intakeEntities = intakeRepository.findByMemberIdAndCreatedAt(requestgetIntake.memberId(),
                requestgetIntake.createdAt());

        return intakeEntities.stream()
                .map(intakeEntity -> {
                    return ResponseGetIntake.builder()
                            .memberId(intakeEntity.getMemberId())
                            .foodId(intakeEntity.getFoodId())
                            .dayTargetCalorieId(intakeEntity.getDayTargetCalorieId())
                            .calculationCarbo(intakeEntity.getCalculationCarbo())
                            .calculationProtein(intakeEntity.getCalculationProtein())
                            .calculationFat(intakeEntity.getCalculationFat())
                            .calculationKcal(intakeEntity.getCalculationKcal())
                            .foodAmount(intakeEntity.getFoodAmount())
                            .category(intakeEntity.getCategory())
                            .build();
                }).collect(Collectors.toList());
    }

    public String deleteIntake(Long intakeId) {
        if(intakeRepository.findById(intakeId)==null){
            throw new ApplicationException(ErrorCode.INTAKE_NOT_FOUND);
        }
        intakeRepository.deleteById(intakeId);
        return "ok";
    }

    @Override
    public List<ResponseGetIntakeRank> getRankList(Long memberId, int period) {

        List<ResponseGetIntakeRank> result = null;

        // db 데이터 조회하기
        LocalDate start = LocalDate.now().minusDays(period);
        LocalDate end = LocalDate.now();
        List<IntakeEntity> intakeList = intakeRepository.findByMemberIdAndCreatedAtBetween(memberId, start, end);

        if(intakeList.isEmpty()) {
            return Collections.emptyList();
        }
        // redis에 데이터 저장하기
        intakeList.stream()
                .forEach((intake) -> {
                    Long foodId = intake.getFoodId();
                    Double findScore = redisIntakeService.getScore(memberId, foodId, period);
                    // key에대한 member가 없으면 새롭게 member생성
                    if(Objects.isNull(findScore)) {
                        int value = 1;
                        redisIntakeService.save(memberId, foodId, period, value);
                        return;
                    }
                    // key에 대한 member가 있으면 1추가
                    redisIntakeService.save(memberId, foodId, period, findScore.intValue() + 1);
                });

        // redis에서 rank 처리하기
        Set<ZSetOperations.TypedTuple<String>> rankRangeSet = redisIntakeService.getRankRangeSet(memberId, period);

        result = rankRangeSet.stream()
                    .map((data) -> {
                        Long foodId = Long.valueOf(data.getValue());
                        // redis에서 name 불러오기
                        Optional<RedisFood> optionalRedisFood = redisFoodRepository.findById(foodId);

                        // TODO : 음식 데이터가 캐시에 없을 경우 처리 로직 만들기
                        if(optionalRedisFood.isEmpty()) {

                        }
                        RedisFood redisFood = optionalRedisFood.get();
                        String name = redisFood.getName();

                        return ResponseGetIntakeRank.builder()
                                .foodId(foodId)
                                .name(name)
                                .value(data.getScore())
                                .build();
                    })
                    .collect(Collectors.toList());

        return result;

    }
}
