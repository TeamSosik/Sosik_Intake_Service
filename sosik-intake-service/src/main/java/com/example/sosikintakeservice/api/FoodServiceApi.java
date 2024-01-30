package com.example.sosikintakeservice.api;

import com.example.sosikintakeservice.dto.api.ResponseGetFood;
import com.example.sosikintakeservice.dto.response.Result;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class FoodServiceApi {

    String uriString = "http://127.0.0.1:9002";

    public Result<ResponseGetFood> getFood(Long foodId) {

        RestTemplate restTemplate = new RestTemplate();
        String path = "/food/v1/" + foodId;

        URI requestUri = UriComponentsBuilder.fromUriString(uriString)
                .path(path)
                .encode()
                .build()
                .toUri();

        RequestEntity<Void> request = RequestEntity.get(requestUri)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        ResponseEntity<Result<ResponseGetFood>> response = restTemplate.exchange(request, new ParameterizedTypeReference<Result<ResponseGetFood>>() {});

        return response.getBody();
    }
}
