package com.orioninc.combplanreviewservice.converter;

import com.orioninc.combplanreviewservice.dto.ReviewDto;
import com.orioninc.combplanreviewservice.entity.Review;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReviewDtoToReview implements Converter<ReviewDto, Review> {
    @Override
    public Review convert(ReviewDto source) {
        Review target = new Review();
        target.setId(source.getId());
        target.setRequestId(source.getRequest().getId());
        target.setUserId(source.getReviewer().getId());
        return target;
    }
}
