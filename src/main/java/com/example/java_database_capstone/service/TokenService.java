package com.example.java_database_capstone.service;

import java.util.Map;

public interface TokenService {
    Map<String, String> validateToken(String token, String userRole);
}