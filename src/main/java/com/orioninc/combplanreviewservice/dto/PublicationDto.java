package com.orioninc.combplanreviewservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class PublicationDto extends AbstractDto {
    private UserDto author;
    private String title;
    private String description;
    private Set<UserDto> coAuthors;
}
