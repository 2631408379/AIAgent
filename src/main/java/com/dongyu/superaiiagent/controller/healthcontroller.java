package com.dongyu.superaiiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("/health")
public class healthcontroller {

    @GetMapping
    public String health() {
        return "OK";
    }
}
