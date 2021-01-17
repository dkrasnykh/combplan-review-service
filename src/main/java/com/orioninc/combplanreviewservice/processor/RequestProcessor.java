package com.orioninc.combplanreviewservice.processor;

import com.orioninc.combplanreviewservice.dto.RequestDto;
import org.apache.kafka.streams.processor.AbstractProcessor;

public class RequestProcessor extends AbstractProcessor<Long, RequestDto> {
    @Override
    public void process(Long aLong, RequestDto requestDto) {

    }
}
