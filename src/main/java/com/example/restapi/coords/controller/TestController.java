package com.example.restapi.coords.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {
    @GetMapping("")
    public String showTestPage() {
        return "test";
    }

}
