package com.campus.campus_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {
    @GetMapping("/") public String home() { return "Backend running successfully!"; }
}
