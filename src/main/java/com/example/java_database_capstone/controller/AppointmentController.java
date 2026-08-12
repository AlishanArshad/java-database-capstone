package com.example.java_database_capstone.controller;

import java.time.LocalDate;
import java.util.HashMap;
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

import com.example.java_database_capstone.entity.Appointment;
import com.example.java_database_capstone.service.AppointmentService;
import com.example.java_database_capstone.service.AuthService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

        private final AppointmentService appointmentService;
        private final AuthService service;

        public AppointmentController(
                        AppointmentService appointmentService,
                        AuthService service) {

                this.appointmentService = appointmentService;
                this.service = service;
        }

        /**
         * Get appointments for a doctor.
         */
        @GetMapping("/{date}/{patientName}/{token}")
        public ResponseEntity<?> getAppointments(
                        @PathVariable LocalDate date,
                        @PathVariable String patientName,
                        @PathVariable String token) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "doctor");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                try {

                        Map<String, Object> appointments = appointmentService.getAppointment(
                                        patientName,
                                        date,
                                        token);

                        return ResponseEntity.ok(appointments);

                } catch (Exception e) {

                        Map<String, String> response = new HashMap<>();

                        response.put(
                                        "message",
                                        "Unable to retrieve appointments");

                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(response);
                }
        }

        /**
         * Book a new appointment.
         */
        @PostMapping("/{token}")
        public ResponseEntity<Map<String, String>> bookAppointment(
                        @PathVariable String token,
                        @RequestBody Appointment appointment) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                int validationResult = service.validateAppointment(appointment);

                if (validationResult == -1) {

                        Map<String, String> response = new HashMap<>();

                        response.put(
                                        "message",
                                        "Doctor not found");

                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(response);
                }

                if (validationResult == 0) {

                        Map<String, String> response = new HashMap<>();

                        response.put(
                                        "message",
                                        "Appointment time is unavailable");

                        return ResponseEntity
                                        .status(HttpStatus.CONFLICT)
                                        .body(response);
                }

                int result = appointmentService.bookAppointment(appointment);

                Map<String, String> response = new HashMap<>();

                if (result == 1) {

                        response.put(
                                        "message",
                                        "Appointment booked successfully");

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(response);
                }

                response.put(
                                "message",
                                "Unable to book appointment");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        /**
         * Update an existing appointment.
         */
        @PutMapping("/{token}")
        public ResponseEntity<Map<String, String>> updateAppointment(
                        @PathVariable String token,
                        @RequestBody Appointment appointment) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                return appointmentService.updateAppointment(
                                appointment);
        }

        /**
         * Cancel an existing appointment.
         */
        @DeleteMapping("/{id}/{token}")
        public ResponseEntity<Map<String, String>> cancelAppointment(
                        @PathVariable long id,
                        @PathVariable String token) {

                ResponseEntity<Map<String, String>> tokenResponse = service.validateToken(token, "patient");

                if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                        return tokenResponse;
                }

                return appointmentService.cancelAppointment(
                                id,
                                token);
        }
}
