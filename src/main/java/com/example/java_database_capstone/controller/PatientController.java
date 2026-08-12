package com.example.java_database_capstone.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_database_capstone.dto.Login;
import com.example.java_database_capstone.entity.Patient;
import com.example.java_database_capstone.service.AuthService;
import com.example.java_database_capstone.service.PatientService;

@RestController
@RequestMapping("/patient")
public class PatientController {

        private final PatientService patientService;
        private final AuthService service;

        public PatientController(
                        PatientService patientService,
                        AuthService service) {

                this.patientService = patientService;
                this.service = service;
        }

        /**
         * Get patient details.
         */
        @GetMapping("/{token}")
        public ResponseEntity<?> getPatientDetails(
                        @PathVariable String token) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                return patientService.getPatientDetails(token);
        }

        /**
         * Create a new patient.
         */
        @PostMapping
        public ResponseEntity<Map<String, String>> createPatient(
                        @RequestBody Patient patient) {

                Map<String, String> response = new HashMap<>();

                try {

                        boolean valid = service.validatePatient(patient);

                        if (!valid) {

                                response.put(
                                                "message",
                                                "Patient with email id or phone no already exist");

                                return ResponseEntity
                                                .status(HttpStatus.CONFLICT)
                                                .body(response);
                        }

                        int result = patientService.createPatient(patient);

                        if (result == 1) {

                                response.put(
                                                "message",
                                                "Signup successful");

                                return ResponseEntity
                                                .status(HttpStatus.CREATED)
                                                .body(response);
                        }

                        response.put(
                                        "message",
                                        "Internal server error");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);

                } catch (Exception e) {

                        response.put(
                                        "message",
                                        "Internal server error");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Patient login.
         */
        @PostMapping("/login")
        public ResponseEntity<Map<String, String>> patientLogin(
                        @RequestBody Login login) {

                return service.validatePatientLogin(login);
        }

        /**
         * Get patient appointments.
         */
        @GetMapping("/{id}/{token}")
        public ResponseEntity<Map<String, Object>> getPatientAppointments(
                        @PathVariable Long id,
                        @PathVariable String token) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {

                        Map<String, Object> response = new HashMap<>();

                        response.put(
                                        "message",
                                        tokenResponse.getBody() != null
                                                        ? tokenResponse.getBody().get("message")
                                                        : "Unauthorized");

                        return ResponseEntity
                                        .status(tokenResponse.getStatusCode())
                                        .body(response);
                }

                return patientService.getPatientAppointment(
                                id,
                                token);
        }

        /**
         * Filter patient appointments.
         */
        @GetMapping("/filter/{condition}/{name}/{token}")
        public ResponseEntity<Map<String, Object>> filterPatientAppointments(
                        @PathVariable String condition,
                        @PathVariable String name,
                        @PathVariable String token) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {

                        Map<String, Object> response = new HashMap<>();

                        response.put(
                                        "message",
                                        tokenResponse.getBody() != null
                                                        ? tokenResponse.getBody().get("message")
                                                        : "Unauthorized");

                        return ResponseEntity
                                        .status(tokenResponse.getStatusCode())
                                        .body(response);
                }

                return service.filterPatient(
                                condition,
                                name,
                                token);
        }
}
