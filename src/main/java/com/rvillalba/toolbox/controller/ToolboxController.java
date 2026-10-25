package com.rvillalba.toolbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.rvillalba.toolbox.api.SampleResponse;
import com.rvillalba.toolbox.service.ToolboxService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ToolboxController {

    private final ToolboxService toolboxService;

    @GetMapping("/sample/{id}")
    public SampleResponse sample(@PathVariable String id) {
        return toolboxService.process(id);
    }
}
