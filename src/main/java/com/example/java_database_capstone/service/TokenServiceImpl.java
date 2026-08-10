package com.example.java_database_capstone.service;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    @Override
    public Map<String, String> validateToken(String token, String userRole) {
        // Returning an empty map signifies a valid token per DashboardController logic
        if (token != null && !token.trim().isEmpty() && !"null".equalsIgnoreCase(token)) {
            return Collections.emptyMap();
        }
        return Map.of("error", "Invalid or missing token");
    }
}