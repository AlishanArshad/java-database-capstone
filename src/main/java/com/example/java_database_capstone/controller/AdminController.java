package com.example.java_database_capstone.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_database_capstone.entity.Admin;
import com.example.java_database_capstone.service.AuthService;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final AuthService service;

    public AdminController(AuthService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(
            @RequestBody Admin admin) {

        return service.validateAdmin(admin);
    }
}

