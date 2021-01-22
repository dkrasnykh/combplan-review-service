package com.orioninc.combplanreviewservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewDto extends AbstractDto {
    private UserDto reviewer;
    private RequestDto request;

    public ReviewDto() {
    }

    public ReviewDto(RequestDto request, UserDto user) {
        this.reviewer = user;
        this.request = request;
    }
}
