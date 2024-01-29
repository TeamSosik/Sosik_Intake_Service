package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.api.FoodServiceApi;
import com.example.sosikintakeservice.dto.IntakeRankCondition;
import com.example.sosikintakeservice.dto.api.ResponseGetFood;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetCreateAt;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntakeRank;
import com.example.sosikintakeservice.dto.response.Result;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import com.example.sosikintakeservice.redis.CacheFood;
import com.example.sosikintakeservice.redis.RedisFoodRepository;
import com.example.sosikintakeservice.repository.IntakeRepository;
import com.example.sosikintakeservice.service.redis.RedisIntakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
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
    private final FoodServiceApi foodServiceApi;

    public String createIntake(Long memberId, RequestIntake intakeDTO) {
        IntakeEntity intake = IntakeEntity.builder()
                .memberId(memberId)
                .foodId(intakeDTO.foodId())
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
                    Optional<CacheFood> optionalRedisFood = redisFoodRepository.findById(intakeEntity.getFoodId());
                    if (optionalRedisFood.isPresent()) {
                        CacheFood redisFood = optionalRedisFood.get();

                        String name = redisFood.getName();
                        System.out.println("==========================");
                        System.out.println(name);
                        System.out.println(optionalRedisFood);

                        return ResponseGetIntake.builder()
                                .id(intakeEntity.getId())
                                .memberId(intakeEntity.getMemberId())
                                .foodId(intakeEntity.getFoodId())
                                .name(redisFood.getName())
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

    @Override
    public List<ResponseGetCreateAt> getCreatedAtList(Long memberId) {
        List<IntakeEntity> intakeEntities = intakeRepository.findByMemberId(memberId);
        return intakeEntities.stream()
                .map(entity -> new ResponseGetCreateAt(entity.getCategory(),entity.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public String deleteIntake(Long intakeId) {
        if(intakeRepository.findById(intakeId)==null){
            throw new ApplicationException(ErrorCode.INTAKE_NOT_FOUND);
        }
        intakeRepository.deleteById(intakeId);
        return "ok";
    }

    @Override
    public List<ResponseGetIntakeRank> getRankList(IntakeRankCondition intakeRankCondition, Long memberId, int period) {

        List<ResponseGetIntakeRank> result = null;

        // db 데이터 조회하기
        LocalDate start = LocalDate.now().minusDays(period);
        LocalDate end = LocalDate.now();
        List<IntakeEntity> intakeList = intakeRepository.findByMemberIdAndCreatedAtBetween(memberId, start, end);

        if(intakeList.isEmpty()) {
            return Collections.emptyList();
        }
        // redis에 기존 데이터 있으면 삭제하기
        redisIntakeService.delete(intakeRankCondition.rankType(), memberId, period);

        // redis에 데이터 저장하기
        saveCache(intakeRankCondition, memberId, period, intakeList);

        // redis에서 rank 불러오기
        Set<ZSetOperations.TypedTuple<String>> rankRangeSet =
                redisIntakeService.getRankRangeSet(intakeRankCondition.rankType() , memberId, period);

        result = rankRangeSet.stream()
                    .map((data) -> {
                        Long foodId = Long.valueOf(data.getValue());
                        // redis에서 name 불러오기
                        String name = "";
                        name = getFoodName(foodId, name);

                        return ResponseGetIntakeRank.builder()
                                .foodId(foodId)
                                .name(name)
                                .value(data.getScore())
                                .build();

                    })
                    .collect(Collectors.toList());

        return result;

    }

    private String getFoodName(Long foodId, String name) {
        Optional<CacheFood> optionalRedisFood = redisFoodRepository.findById(foodId);

        if(!optionalRedisFood.isEmpty()) {
            CacheFood redisFood = optionalRedisFood.get();
            name = redisFood.getName();
        }

        // 음식 데이터가 캐시에 없을 경우 처리 로직 만들기
        if(optionalRedisFood.isEmpty()) {
            // 음식 DB요청하기
            // TODO : 조회했을 때 데이터가 없으면 어떻게 처리할 지 생각해보기
            Result<ResponseGetFood> resultResponseGetFood = null;
            try {
                resultResponseGetFood = foodServiceApi.getFood(foodId);

                if(Objects.isNull(resultResponseGetFood.getResult())) {
                    throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
                }

                name = resultResponseGetFood.getResult().getName();

                // redis에 저장하기
                CacheFood cacheFood = CacheFood.create(resultResponseGetFood.getResult());
                redisFoodRepository.save(cacheFood);
            // connection 에러 처리
            } catch (RestClientException e) {
                throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return name;
    }

    private void saveCache(IntakeRankCondition intakeRankCondition, Long memberId, int period, List<IntakeEntity> intakeList) {

        if("food".equalsIgnoreCase(intakeRankCondition.rankType())) {
            intakeList.stream()
                    .forEach((intake) -> {
                        Long foodId = intake.getFoodId();
                        Double findScore = redisIntakeService.getScore(intakeRankCondition.rankType(), memberId, foodId, period);
                        // key에대한 member가 없으면 새롭게 member생성
                        if (Objects.isNull(findScore)) {
                            int value = 1;
                            Double doubleValue = Double.valueOf(value);
                            redisIntakeService.save(intakeRankCondition.rankType(), memberId, foodId, period, doubleValue);
                            return;
                        }
                        // key에 대한 member가 있으면 1추가
                        Double doubleValue = Double.valueOf(findScore.intValue() + 1);
                        redisIntakeService.save(intakeRankCondition.rankType(), memberId, foodId, period, doubleValue);
                    });
            return;
        }
        if("kcal".equalsIgnoreCase(intakeRankCondition.rankType())) {
            intakeList.stream()
                    .forEach((intake) -> {
                        Long foodId = intake.getFoodId();
                        Double findScore = redisIntakeService.getScore(intakeRankCondition.rankType(), memberId, foodId, period);
                        BigDecimal value = intake.getCalculationKcal();
                        // key에대한 member가 없으면 새롭게 member생성
                        if (Objects.isNull(findScore)) {

                            Double doubleValue = Double.valueOf(String.valueOf(value));
                            redisIntakeService.save(intakeRankCondition.rankType(), memberId, foodId, period, doubleValue);
                            return;
                        }
                        // key에 대한 member가 있으면 1추가
                        Double doubleValue = findScore + Double.valueOf(String.valueOf(value));
                        redisIntakeService.save(intakeRankCondition.rankType(), memberId, foodId, period, doubleValue);
                    });
            return;
        }
    }
}
