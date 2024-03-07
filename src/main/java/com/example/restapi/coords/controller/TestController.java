package com.example.restapi.coords.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {
    @GetMapping("/test")
    public String showTestPage(Model model) {
        model.addAttribute("hello","안눙!");
        return "test";
    }

    @GetMapping("/")
    public String showIndexPage(Model model) {
        model.addAttribute("hello","안눙!");
        return "index";
    }

}
