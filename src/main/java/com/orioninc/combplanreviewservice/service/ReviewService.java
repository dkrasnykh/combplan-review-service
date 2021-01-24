package com.orioninc.combplanreviewservice.service;

import com.orioninc.combplanreviewservice.dto.ReviewDto;

public interface ReviewService {
    ReviewDto getReviewById(Long id);

    ReviewDto createReview(ReviewDto reviewDto);

    ReviewDto updateReview(ReviewDto reviewDto);

    void deleteReview(Long id);
}
