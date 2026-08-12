package com.example.java_database_capstone.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_database_capstone.dto.Login;
import com.example.java_database_capstone.entity.Doctor;
import com.example.java_database_capstone.service.AuthService;
import com.example.java_database_capstone.service.DoctorService;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

        private final DoctorService doctorService;
        private final AuthService service;

        public DoctorController(
                        DoctorService doctorService,
                        AuthService service) {

                this.doctorService = doctorService;
                this.service = service;
        }

        /**
         * Get doctor availability.
         */
        @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
        public ResponseEntity<?> getDoctorAvailability(
                        @PathVariable String user,
                        @PathVariable Long doctorId,
                        @PathVariable LocalDate date,
                        @PathVariable String token) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, user);

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                try {

                        List<String> availability = doctorService.getDoctorAvailability(
                                        doctorId,
                                        date);

                        Map<String, Object> response = new HashMap<>();

                        response.put("availability", availability);

                        return ResponseEntity.ok(response);

                } catch (Exception e) {

                        Map<String, String> response = new HashMap<>();

                        response.put(
                                        "message",
                                        "Unable to get doctor availability");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Get all doctors.
         */
        @GetMapping
        public ResponseEntity<Map<String, Object>> getDoctors() {

                Map<String, Object> response = new HashMap<>();

                response.put(
                                "doctors",
                                doctorService.getDoctors());

                return ResponseEntity.ok(response);
        }

        /**
         * Add a new doctor.
         */
        @PostMapping("/{token}")
        public ResponseEntity<Map<String, String>> addDoctor(
                        @PathVariable String token,
                        @RequestBody Doctor doctor) {

                Map<String, String> response = new HashMap<>();

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                int result = doctorService.saveDoctor(doctor);

                if (result == 1) {

                        response.put(
                                        "message",
                                        "Doctor added to db");

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(response);
                }

                if (result == -1) {

                        response.put(
                                        "message",
                                        "Doctor already exists");

                        return ResponseEntity
                                        .status(HttpStatus.CONFLICT)
                                        .body(response);
                }

                response.put(
                                "message",
                                "Some internal error occurred");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        /**
         * Doctor login.
         */
        @PostMapping("/login")
        public ResponseEntity<Map<String, String>> doctorLogin(
                        @RequestBody Login login) {

                return doctorService.validateDoctor(login);
        }

        /**
         * Update doctor details.
         */
        @PutMapping("/{token}")
        public ResponseEntity<Map<String, String>> updateDoctor(
                        @PathVariable String token,
                        @RequestBody Doctor doctor) {

                Map<String, String> response = new HashMap<>();

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                int result = doctorService.updateDoctor(doctor);

                if (result == 1) {

                        response.put(
                                        "message",
                                        "Doctor updated");

                        return ResponseEntity.ok(response);
                }

                if (result == -1) {

                        response.put(
                                        "message",
                                        "Doctor not found");

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(response);
                }

                response.put(
                                "message",
                                "Some internal error occurred");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        /**
         * Delete doctor.
         */
        @DeleteMapping("/{id}/{token}")
        public ResponseEntity<Map<String, String>> deleteDoctor(
                        @PathVariable long id,
                        @PathVariable String token) {

                Map<String, String> response = new HashMap<>();

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "admin");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                int result = doctorService.deleteDoctor(id);

                if (result == 1) {

                        response.put(
                                        "message",
                                        "Doctor deleted successfully");

                        return ResponseEntity.ok(response);
                }

                if (result == -1) {

                        response.put(
                                        "message",
                                        "Doctor not found with id");

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(response);
                }

                response.put(
                                "message",
                                "Some internal error occurred");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        /**
         * Filter doctors.
         */
        @GetMapping("/filter/{name}/{time}/{speciality}")
        public ResponseEntity<Map<String, Object>> filterDoctors(
                        @PathVariable String name,
                        @PathVariable String time,
                        @PathVariable String speciality) {

                Map<String, Object> response = service.filterDoctor(
                                name,
                                speciality,
                                time);

                return ResponseEntity.ok(response);
        }
}
