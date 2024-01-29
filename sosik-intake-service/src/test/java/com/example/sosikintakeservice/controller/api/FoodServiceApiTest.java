package com.example.sosikintakeservice.controller.api;

import com.example.sosikintakeservice.api.FoodServiceApi;
import com.example.sosikintakeservice.dto.api.ResponseGetFood;
import com.example.sosikintakeservice.dto.response.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FoodServiceApiTest {

    @Autowired
    FoodServiceApi foodServiceApi;

    // TODO : 어떻게 하면 통과가 될지 생각해보기
//    @DisplayName("음식서비스와_연결이끊어졌을때_확인")
//    @Test
//    void FoodServiceConnectionError() {
//
//        // given
//        Long foodId = 1L;
//
//        // when
//        assertThrows(ResourceAccessException.class,
//                () -> {foodServiceApi.getFood(foodId);}
//        );
//    }

    @DisplayName("음식데이터_가져오는지_확인")
    @Test
    void getFoodTest() {

        // given
        Long foodId = 1L;

        // when
        Result<ResponseGetFood> result = foodServiceApi.getFood(foodId);

        // then
        ResponseGetFood responseGetFood = result.getResult();
        Assertions.assertThat(responseGetFood.getFoodId()).isEqualTo(foodId);
    }



}
