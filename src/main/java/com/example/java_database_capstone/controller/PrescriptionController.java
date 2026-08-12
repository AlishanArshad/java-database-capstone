package com.example.java_database_capstone.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.java_database_capstone.entity.Prescription;
import com.example.java_database_capstone.service.AuthService;
import com.example.java_database_capstone.service.PrescriptionService;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AuthService service;

    // Save prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return prescriptionService.savePrescription(prescription);
    }

    // Get prescription by appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity
                    .status(validation.getStatusCode())
                    .body(Map.of(
                            "message",
                            validation.getBody() != null
                                    ? validation.getBody().get("message")
                                    : "Unauthorized"));
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}