package com.orioninc.combplanreviewservice.converter;

import org.springframework.core.convert.support.ConfigurableConversionService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface CustomConversionService extends ConfigurableConversionService {
    default <T, S> List<T> convert(List<S> sources, Class<T> targetClass) {
        return Objects.isNull(sources) ?
                Collections.emptyList() :
                sources.stream()
                        .map(source -> convert(source, targetClass))
                        .collect(Collectors.toList());
    }

    default <T, S> Set<T> convert(Set<S> sources, Class<T> targetClass) {
        return Objects.isNull(sources) ?
                Collections.emptySet() :
                sources.stream()
                        .map(source -> convert(source, targetClass))
                        .collect(Collectors.toSet());
    }
}
