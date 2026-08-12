package com.example.java_database_capstone.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.java_database_capstone.entity.Admin;
import com.example.java_database_capstone.entity.Doctor;
import com.example.java_database_capstone.entity.Patient;
import com.example.java_database_capstone.repository.AdminRepository;
import com.example.java_database_capstone.repository.DoctorRepository;
import com.example.java_database_capstone.repository.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Provide a fallback default so tests or runs without an external property
    // do not fail bean creation. The default is long enough for HMAC-SHA (>=32 bytes).
    @Value("${jwt.secret:default_jwt_secret_must_be_32_bytes_minimum_0123456789abcdef}")
    private String secret;

    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {

        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Generate JWT token.
     *
     * identifier can be:
     * - Admin username
     * - Doctor email
     * - Patient email
     */
    public String generateToken(String identifier) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime()
                        + (7L * 24 * 60 * 60 * 1000)
        );

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract identifier from JWT.
     */
    public String extractIdentifier(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Extract email from JWT.
     *
     * Doctor and Patient tokens use email
     * as their JWT subject.
     */
    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    /**
     * Get user ID from JWT.
     *
     * The JWT subject contains the user's email/username,
     * so the corresponding repository is used to find the ID.
     */
    public Long getUserId(String token) {

        try {

            String identifier = extractIdentifier(token);

            if (identifier == null || identifier.isBlank()) {
                return null;
            }

            Doctor doctor = doctorRepository.findByEmail(identifier);

            if (doctor != null) {
                return doctor.getId();
            }

            Patient patient = patientRepository.findByEmail(identifier);

            if (patient != null) {
                return patient.getId();
            }

            Admin admin = adminRepository.findByUsername(identifier);

            if (admin != null) {
                return admin.getId();
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validate JWT token for a specific user role.
     */
    public boolean validateToken(
            String token,
            String userRole) {

        try {

            if (token == null || token.isBlank()) {
                return false;
            }

            String identifier = extractIdentifier(token);

            if (identifier == null || identifier.isBlank()) {
                return false;
            }

            if (userRole == null || userRole.isBlank()) {
                return false;
            }

            switch (userRole.toLowerCase()) {

                case "admin":

                    Admin admin =
                            adminRepository.findByUsername(identifier);

                    return admin != null;

                case "doctor":

                    Doctor doctor =
                            doctorRepository.findByEmail(identifier);

                    return doctor != null;

                case "patient":

                    Patient patient =
                            patientRepository.findByEmail(identifier);

                    return patient != null;

                default:
                    return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get JWT signing key.
     */
    public SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}