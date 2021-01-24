package com.orioninc.combplanreviewservice.service;

import com.orioninc.combplanreviewservice.converter.CustomConversionService;
import com.orioninc.combplanreviewservice.dto.ReviewDto;
import com.orioninc.combplanreviewservice.entity.Review;
import com.orioninc.combplanreviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository repository;
    private final CustomConversionService conversionService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository repository, CustomConversionService conversionService) {
        this.repository = repository;
        this.conversionService = conversionService;
    }

    @Override
    public ReviewDto getReviewById(Long id) {
        Review review = repository.findById(id).orElseThrow(() -> new RuntimeException("Review is not found"));
        return conversionService.convert(review, ReviewDto.class);
    }

    @Override
    public ReviewDto createReview(ReviewDto reviewDto) {
        Review review = conversionService.convert(reviewDto, Review.class);
        return conversionService.convert(repository.save(review), ReviewDto.class);
    }

    @Override
    public ReviewDto updateReview(ReviewDto reviewDto) {
        Review review = repository.findById(reviewDto.getId()).orElseThrow(() -> new RuntimeException("Review is not found"));
        review.setUserId(reviewDto.getReviewer().getId());
        review.setRequestId(reviewDto.getRequest().getId());
        return conversionService.convert(repository.save(review), ReviewDto.class);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = repository.findById(id).orElseThrow(() -> new RuntimeException("Review is not found"));
        repository.delete(review);
    }
}
