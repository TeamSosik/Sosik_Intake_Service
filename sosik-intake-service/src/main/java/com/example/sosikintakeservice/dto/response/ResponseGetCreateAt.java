package com.example.sosikintakeservice.dto.response;

import com.example.sosikintakeservice.model.entity.Category;

import java.time.LocalDate;

public record ResponseGetCreateAt(
        Category category,
        LocalDate createdAt) {
}
