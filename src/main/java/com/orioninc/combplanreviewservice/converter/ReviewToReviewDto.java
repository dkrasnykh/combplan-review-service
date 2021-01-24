package com.orioninc.combplanreviewservice.converter;

import com.orioninc.combplanreviewservice.dto.RequestDto;
import com.orioninc.combplanreviewservice.dto.ReviewDto;
import com.orioninc.combplanreviewservice.dto.UserDto;
import com.orioninc.combplanreviewservice.entity.Review;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReviewToReviewDto implements Converter<Review, ReviewDto> {
    @Override
    public ReviewDto convert(Review source) {
        ReviewDto target = new ReviewDto();
        target.setId(source.getId());
        RequestDto requestDto = new RequestDto();
        requestDto.setId(source.getRequestId());
        target.setRequest(requestDto);
        UserDto userDto = new UserDto();
        userDto.setId(source.getUserId());
        target.setReviewer(userDto);
        return target;
    }
}
