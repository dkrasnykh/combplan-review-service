package com.orioninc.combplanreviewservice.converter.impl;

import com.orioninc.combplanreviewservice.converter.CustomConversionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Service;

@Service
@Qualifier("customConversionService")
public class CustomConversionServiceImpl extends GenericConversionService implements CustomConversionService {
}
