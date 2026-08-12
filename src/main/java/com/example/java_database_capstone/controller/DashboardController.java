package com.example.java_database_capstone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.java_database_capstone.service.TokenService;

@Controller
public class DashboardController {

    private final TokenService tokenService;

    public DashboardController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Validates admin token and serves the Admin Dashboard template.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {

        boolean valid = tokenService.validateToken(token, "admin");

        if (valid) {
            return "admin/adminDashboard";
        }

        return "redirect:http://localhost:8080";
    }

    /**
     * Validates doctor token and serves the Doctor Dashboard template.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {

        boolean valid =
                tokenService.validateToken(token, "doctor");

        if (valid) {
            return "doctor/doctorDashboard";
        }

        return "redirect:http://localhost:8080";
    }
}