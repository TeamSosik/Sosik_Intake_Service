package com.example.sosikintakeservice.service;

import com.example.sosikintakeservice.dto.request.RequestGetIntake;
import com.example.sosikintakeservice.dto.request.RequestIntake;
import com.example.sosikintakeservice.dto.response.ResponseGetIntake;
import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorCode;
import com.example.sosikintakeservice.model.entity.IntakeEntity;
import com.example.sosikintakeservice.repository.IntakeRepository;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IntakeServiceImpl implements IntakeService{
    @Autowired
    private IntakeRepository intakeRepository;

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

    public ResponseGetIntake getIntake(Long intakeId) {
        IntakeEntity intakeEntity = intakeRepository.findById(intakeId)
                .orElseThrow(IllegalArgumentException::new);

        return new ResponseGetIntake(
                intakeEntity.getMemberId(),
                intakeEntity.getFoodId(),
                intakeEntity.getDayTargetCalorieId(),
                intakeEntity.getCalculationCarbo(),
                intakeEntity.getCalculationProtein(),
                intakeEntity.getCalculationFat(),
                intakeEntity.getCalculationKcal(),
                intakeEntity.getFoodAmount(),
                intakeEntity.getCategory()
        );
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

}
