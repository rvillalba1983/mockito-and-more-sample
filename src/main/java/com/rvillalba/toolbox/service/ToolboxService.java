package com.rvillalba.toolbox.service;

import org.springframework.stereotype.Service;

import com.rvillalba.toolbox.api.SampleResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToolboxService {

    static final String ID_PREFIX = "Id";
    static final String MESSAGE_PREFIX = "The id is ";

    private final AddressExternalService addressExternalService;

    public SampleResponse process(String id) {
        return SampleResponse.builder()
                .message(MESSAGE_PREFIX + normalizeId(id))
                .address(addressExternalService.findAddress(id))
                .build();
    }

    private String normalizeId(String id) {
        return id.startsWith(ID_PREFIX) ? id.toUpperCase() : id.toLowerCase();
    }
}
