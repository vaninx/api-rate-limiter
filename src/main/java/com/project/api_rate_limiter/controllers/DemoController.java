package com.project.api_rate_limiter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/demo/hello")
    public String hello(){
        return "hello world!";
    }
}
